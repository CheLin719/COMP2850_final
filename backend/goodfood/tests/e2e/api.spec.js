// 测试 api.js 里的 NW 对象和 API 封装
// Test the NW object and API encapsulation within api.js


const { test, expect } = require('@playwright/test')

test.describe('NourishWell API client tests', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:8080/index.html')
    await page.evaluate(() => sessionStorage.clear())
  })

  test('NW object should exist on window', async ({ page }) => {
    const exists = await page.evaluate(() => typeof window.NW !== 'undefined')

    expect(exists).toBe(true)
  })

  test('auth save should store user data in sessionStorage', async ({ page }) => {
    await page.evaluate(() => {
      window.NW.auth.save({
        token: 'test-token',
        userId: 1,
        role: 'subscriber',
        firstName: 'Tengchuan',
        lastName: 'Jiang'
      })
    })

    const result = await page.evaluate(() => ({
      token: sessionStorage.getItem('nw-token'),
      userId: sessionStorage.getItem('nw-userId'),
      role: sessionStorage.getItem('nw-role'),
      name: sessionStorage.getItem('nw-name')
    }))

    expect(result.token).toBe('test-token')
    expect(result.userId).toBe('1')
    expect(result.role).toBe('subscriber')
    expect(result.name).toBe('Tengchuan Jiang')
  })

  test('auth clear should remove user data', async ({ page }) => {
    await page.evaluate(() => {
      window.NW.auth.save({
        token: 'test-token',
        userId: 1,
        role: 'subscriber',
        name: 'Test User'
      })
      window.NW.auth.clear()
    })

    const result = await page.evaluate(() => ({
      token: sessionStorage.getItem('nw-token'),
      userId: sessionStorage.getItem('nw-userId'),
      role: sessionStorage.getItem('nw-role'),
      name: sessionStorage.getItem('nw-name')
    }))

    expect(result.token).toBeNull()
    expect(result.userId).toBeNull()
    expect(result.role).toBeNull()
    expect(result.name).toBeNull()
  })

  test('isLoggedIn should return true when token exists', async ({ page }) => {
    const result = await page.evaluate(() => {
      window.NW.auth.save({
        token: 'abc123',
        userId: 1,
        role: 'subscriber'
      })

      return window.NW.auth.isLoggedIn()
    })

    expect(result).toBe(true)
  })

  test('isPro should return true for professional role', async ({ page }) => {
    const result = await page.evaluate(() => {
      window.NW.auth.save({
        token: 'abc123',
        userId: 2,
        role: 'professional'
      })

      return window.NW.auth.isPro()
    })

    expect(result).toBe(true)
  })

  test('login should call API and save returned data', async ({ page }) => {
    await page.route('**/api/auth/login', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          userId: 1,
          token: 'login-token',
          role: 'subscriber',
          firstName: 'Tengchuan',
          lastName: 'Jiang'
        })
      })
    })

    const result = await page.evaluate(async () => {
      return await window.NW.login('Tengchuan@example.com', 'Password123!')
    })

    const storage = await page.evaluate(() => ({
      token: sessionStorage.getItem('nw-token'),
      userId: sessionStorage.getItem('nw-userId'),
      role: sessionStorage.getItem('nw-role'),
      name: sessionStorage.getItem('nw-name')
    }))

    expect(result.token).toBe('login-token')
    expect(storage.token).toBe('login-token')
    expect(storage.userId).toBe('1')
    expect(storage.role).toBe('subscriber')
    expect(storage.name).toBe('Tengchuan Jiang')
  })

  test('register should call API and save returned data', async ({ page }) => {
    await page.route('**/api/auth/register', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          userId: 3,
          token: 'register-token',
          role: 'professional',
          firstName: 'Alex',
          lastName: 'Green'
        })
      })
    })

    const result = await page.evaluate(async () => {
      return await window.NW.register(
        'Alex',
        'Green',
        'alex@example.com',
        'Password123!',
        'professional',
        'NUT-2026-001'
      )
    })

    const storage = await page.evaluate(() => ({
      token: sessionStorage.getItem('nw-token'),
      userId: sessionStorage.getItem('nw-userId'),
      role: sessionStorage.getItem('nw-role'),
      name: sessionStorage.getItem('nw-name')
    }))

    expect(result.token).toBe('register-token')
    expect(storage.token).toBe('register-token')
    expect(storage.userId).toBe('3')
    expect(storage.role).toBe('professional')
    expect(storage.name).toBe('Alex Green')
  })

  test('login should throw error when API returns 401', async ({ page }) => {
    await page.route('**/api/auth/login', async route => {
      await route.fulfill({
        status: 401,
        contentType: 'application/json',
        body: JSON.stringify({
          message: 'Invalid login'
        })
      })
    })

    const result = await page.evaluate(async () => {
      try {
        await window.NW.login('wrong@example.com', 'wrongpassword')
        return 'success'
      } catch (err) {
        return {
          message: err.message,
          status: err.status
        }
      }
    })

    expect(result.message).toBe('AUTH_ERROR')
    expect(result.status).toBe(401)
  })

  test('diary get should send authorization token', async ({ page }) => {
    await page.evaluate(() => {
      window.NW.auth.save({
        token: 'test-token',
        userId: 1,
        role: 'subscriber'
      })
    })

    await page.route('**/api/diary?date=2026-04-20', async route => {
      const headers = route.request().headers()

      expect(headers.authorization).toBe('Bearer test-token')

      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          meals: [
            {
              id: 1,
              mealType: 'BREAKFAST',
              foodName: 'Oats',
              kcal: 350,
              protein: 12,
              carbs: 50,
              fat: 8,
              sugar: 5
            }
          ]
        })
      })
    })

    const result = await page.evaluate(async () => {
      return await window.NW.diary.get('2026-04-20')
    })

    expect(result.meals.length).toBe(1)
    expect(result.meals[0].foodName).toBe('Oats')
  })

  test('parseDiaryToMealLog should convert API meal data', async ({ page }) => {
    const result = await page.evaluate(() => {
      return window.NW.parseDiaryToMealLog([
        {
          id: 5,
          mealType: 'BREAKFAST',
          foodName: 'Greek Yogurt',
          kcal: 220,
          protein: 18,
          carbs: 20,
          fat: 6,
          sugar: 8
        },
        {
          id: 6,
          mealType: 'DINNER',
          foodName: 'Salmon Rice Bowl',
          kcal: 620,
          protein: 35,
          carbs: 70,
          fat: 18,
          sugar: 4
        }
      ])
    })

    expect(result.breakfast.length).toBe(1)
    expect(result.dinner.length).toBe(1)
    expect(result.breakfast[0].name).toBe('Greek Yogurt')
    expect(result.dinner[0].name).toBe('Salmon Rice Bowl')
  })

  test('parseRecipes should convert API recipes into front-end format', async ({ page }) => {
    const result = await page.evaluate(() => {
      return window.NW.parseRecipes([
        {
          id: 10,
          name: 'Avocado Toast',
          emoji: '🥑',
          tag: 'Breakfast',
          kcal: 290,
          cost: '2.50',
          timeMin: 10,
          ingredients: ['Bread', 'Avocado'],
          steps: ['Toast bread', 'Add avocado'],
          averageRating: 4,
          ratingCount: 12,
          commentCount: 3
        }
      ])
    })

    expect(result['10'].name).toBe('Avocado Toast')
    expect(result['10'].emoji).toBe('🥑')
    expect(result['10'].kcal).toBe(290)
    expect(result['10'].cost).toBe('£2.50')
    expect(result['10'].rating).toBe('★★★★☆')
    expect(result['10'].commentCount).toBe(3)
  })
})