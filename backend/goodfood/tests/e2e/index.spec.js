// 主要测试首页、登录、注册表单交互
// Mainly test the interaction of the homepage, login, and registration forms.
/*
1. 登录时是否调用 /api/auth/login
2. 注册时是否调用 /api/auth/register
3. 登录成功后是否显示 success overlay
4. 登录失败时是否显示错误信息
5. 注册密码是否必须满足强密码规则
6. Professional 注册是否必须填写 licence number
7. 注册成功后是否显示 success overlay
8. api.js 是否正确保存 token / role / userId
1. Is /api/auth/login called when logging in?
2. Is /api/auth/register called when registering?
3. Is a success overlay displayed after successful login?
4. Is an error message displayed when login fails?
5. Must the registration password meet strong password rules?
6. Is the licence number mandatory for Professional registration?
7. Is a success overlay displayed after successful registration?
8. Is the token/role/userId correctly saved in api.js?
*/


const { test, expect } = require('@playwright/test')

test.describe('NourishWell index page front-end tests', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:8080/index.html')
    await page.evaluate(() => sessionStorage.clear())
  })

  test('landing page should display main content', async ({ page }) => {
    await expect(page.locator('.logo-text')).toHaveText('NourishWell')
    await expect(page.locator('.hero-super')).toHaveText('GOOD FOOD & HEALTHY EATING')
    await expect(page.locator('.hero-title')).toContainText('Eat with')
    await expect(page.locator('.hero-title')).toContainText('Feel well')
    await expect(page.locator('.hero-sub')).toContainText('Log your meals')
    await expect(page.locator('.hero-btn-main')).toContainText('Start for free')
  })

  test('clicking login should show login form', async ({ page }) => {
    await page.locator('.nav-btn-ghost').click()

    await expect(page.locator('body')).toHaveClass(/show-auth/)
    await expect(page.locator('#page-login')).toBeVisible()
    await expect(page.locator('#page-register')).toHaveClass(/hidden/)
    await expect(page.locator('#login-email')).toBeVisible()
    await expect(page.locator('#login-password')).toBeVisible()
    await expect(page.locator('#login-btn')).toContainText('Sign in')
  })

  test('clicking get started should show register form', async ({ page }) => {
    await page.locator('.nav-btn-fill').click()

    await expect(page.locator('body')).toHaveClass(/show-auth/)
    await expect(page.locator('#page-register')).toBeVisible()
    await expect(page.locator('#page-login')).toHaveClass(/hidden/)
    await expect(page.locator('#reg-first')).toBeVisible()
    await expect(page.locator('#reg-last')).toBeVisible()
    await expect(page.locator('#reg-email')).toBeVisible()
    await expect(page.locator('#reg-password')).toBeVisible()
    await expect(page.locator('#register-btn')).toContainText('Create account')
  })

  test('login form should show validation errors when empty', async ({ page }) => {
    await page.locator('.nav-btn-ghost').click()
    await page.locator('#login-btn').click()

    await expect(page.locator('#login-email-err')).toHaveClass(/show/)
    await expect(page.locator('#login-pw-err')).toHaveClass(/show/)
    await expect(page.locator('#login-email')).toHaveAttribute('aria-invalid', 'true')
    await expect(page.locator('#login-password')).toHaveAttribute('aria-invalid', 'true')
  })

  test('login should call API and show success overlay when valid', async ({ page }) => {
    await page.route('**/api/auth/login', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          userId: 1,
          token: 'test-token',
          role: 'subscriber',
          firstName: 'Tengchuan',
          lastName: 'Jiang'
        })
      })
    })

    await page.locator('.nav-btn-ghost').click()
    await page.locator('#login-email').fill('tengchuan@example.com')
    await page.locator('#login-password').fill('Password123!')
    await page.locator('#login-btn').click()

    await expect(page.locator('#login-success')).toHaveClass(/show/)
    await expect(page.locator('#login-success')).toContainText('Welcome back')
    await expect(page.locator('#login-form-wrap')).toBeHidden()

    const storage = await page.evaluate(() => ({
      token: sessionStorage.getItem('nw-token'),
      userId: sessionStorage.getItem('nw-userId'),
      role: sessionStorage.getItem('nw-role'),
      name: sessionStorage.getItem('nw-name')
    }))

    expect(storage.token).toBe('test-token')
    expect(storage.userId).toBe('1')
    expect(storage.role).toBe('subscriber')
    expect(storage.name).toBe('Tengchuan Jiang')
  })

  test('login should show error when API returns 401', async ({ page }) => {
    await page.route('**/api/auth/login', async route => {
      await route.fulfill({
        status: 401,
        contentType: 'application/json',
        body: JSON.stringify({
          message: 'Incorrect email or password.'
        })
      })
    })

    await page.locator('.nav-btn-ghost').click()
    await page.locator('#login-email').fill('wrong@example.com')
    await page.locator('#login-password').fill('wrongpassword')
    await page.locator('#login-btn').click()

    await expect(page.locator('#login-pw-err')).toHaveClass(/show/)
    await expect(page.locator('#login-pw-err')).toContainText('Incorrect email or password')
    await expect(page.locator('#login-password')).toHaveAttribute('aria-invalid', 'true')
  })

  test('register form should show password requirements', async ({ page }) => {
    await page.locator('.nav-btn-fill').click()

    await expect(page.locator('#pw-requirements')).toBeVisible()
    await expect(page.locator('#pw-requirements')).toContainText('At least')
    await expect(page.locator('#pw-requirements')).toContainText('uppercase')
    await expect(page.locator('#pw-requirements')).toContainText('lowercase')
    await expect(page.locator('#pw-requirements')).toContainText('number')
    await expect(page.locator('#pw-requirements')).toContainText('special character')
  })

  test('password requirements should update when password is typed', async ({ page }) => {
    await page.locator('.nav-btn-fill').click()
    await page.locator('#reg-password').fill('Password123!')

    await expect(page.locator('#pw-req-len')).toHaveClass(/met/)
    await expect(page.locator('#pw-req-upper')).toHaveClass(/met/)
    await expect(page.locator('#pw-req-lower')).toHaveClass(/met/)
    await expect(page.locator('#pw-req-digit')).toHaveClass(/met/)
    await expect(page.locator('#pw-req-special')).toHaveClass(/met/)
  })

  test('register form should reject weak password', async ({ page }) => {
    await page.locator('.nav-btn-fill').click()

    await page.locator('#reg-first').fill('Tengchuan')
    await page.locator('#reg-last').fill('Jiang')
    await page.locator('#reg-email').fill('tengchuan@example.com')
    await page.locator('#reg-password').fill('password')
    await page.locator('#register-btn').click()

    await expect(page.locator('#reg-pw-err')).toHaveClass(/show/)
    await expect(page.locator('#reg-pw-err')).toContainText('Please meet all password requirements')
    await expect(page.locator('#reg-password')).toHaveAttribute('aria-invalid', 'true')
  })

  test('selecting professional role should show licence field', async ({ page }) => {
    await page.locator('.nav-btn-fill').click()
    await page.locator('#role-pro').click()

    await expect(page.locator('#role-pro')).toHaveClass(/active/)
    await expect(page.locator('#role-pro')).toHaveAttribute('aria-checked', 'true')
    await expect(page.locator('#role-sub')).toHaveAttribute('aria-checked', 'false')
    await expect(page.locator('#pro-field')).toHaveClass(/visible/)
    await expect(page.locator('#reg-licence')).toBeVisible()
  })

  test('professional registration should require licence number', async ({ page }) => {
    await page.locator('.nav-btn-fill').click()
    await page.locator('#role-pro').click()

    await page.locator('#reg-first').fill('Tengchuan')
    await page.locator('#reg-last').fill('Jiang')
    await page.locator('#reg-email').fill('tengchuan@example.com')
    await page.locator('#reg-password').fill('Password123!')
    await page.locator('#register-btn').click()

    await expect(page.locator('#reg-licence-err')).toHaveClass(/show/)
    await expect(page.locator('#reg-licence')).toHaveAttribute('aria-invalid', 'true')
  })

  test('subscriber registration should call API and show success overlay', async ({ page }) => {
    await page.route('**/api/auth/register', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          userId: 2,
          token: 'register-token',
          role: 'subscriber',
          firstName: 'Tengchuan',
          lastName: 'Jiang'
        })
      })
    })

    await page.locator('.nav-btn-fill').click()

    await page.locator('#reg-first').fill('Tengchuan')
    await page.locator('#reg-last').fill('Jiang')
    await page.locator('#reg-email').fill('tengchuan@example.com')
    await page.locator('#reg-password').fill('Password123!')
    await page.locator('#register-btn').click()

    await expect(page.locator('#register-success')).toHaveClass(/show/)
    await expect(page.locator('#register-success')).toContainText("You're all set")
    await expect(page.locator('#register-form-wrap')).toBeHidden()

    const storage = await page.evaluate(() => ({
      token: sessionStorage.getItem('nw-token'),
      userId: sessionStorage.getItem('nw-userId'),
      role: sessionStorage.getItem('nw-role'),
      name: sessionStorage.getItem('nw-name')
    }))

    expect(storage.token).toBe('register-token')
    expect(storage.userId).toBe('2')
    expect(storage.role).toBe('subscriber')
    expect(storage.name).toBe('Tengchuan Jiang')
  })

  test('register should show email error when API returns 409', async ({ page }) => {
    await page.route('**/api/auth/register', async route => {
      await route.fulfill({
        status: 409,
        contentType: 'application/json',
        body: JSON.stringify({
          message: 'This email is already registered.'
        })
      })
    })

    await page.locator('.nav-btn-fill').click()

    await page.locator('#reg-first').fill('Tengchuan')
    await page.locator('#reg-last').fill('Jiang')
    await page.locator('#reg-email').fill('used@example.com')
    await page.locator('#reg-password').fill('Password123!')
    await page.locator('#register-btn').click()

    await expect(page.locator('#reg-email-err')).toHaveClass(/show/)
    await expect(page.locator('#reg-email-err')).toContainText('already registered')
    await expect(page.locator('#reg-email')).toHaveAttribute('aria-invalid', 'true')
  })

  test('password visibility button should toggle password input type', async ({ page }) => {
    await page.locator('.nav-btn-ghost').click()

    await expect(page.locator('#login-password')).toHaveAttribute('type', 'password')
    await page.locator('#page-login .pw-toggle').click()
    await expect(page.locator('#login-password')).toHaveAttribute('type', 'text')
    await expect(page.locator('#page-login .pw-toggle')).toHaveAttribute('aria-label', 'Hide password')
  })

  test('back button should return from auth page to landing page', async ({ page }) => {
    await page.locator('.nav-btn-ghost').click()
    await expect(page.locator('body')).toHaveClass(/show-auth/)

    await page.locator('.auth-back-btn').click()

    await expect(page.locator('body')).not.toHaveClass(/show-auth/)
    await expect(page.locator('#page-landing')).toBeVisible()
  })
})