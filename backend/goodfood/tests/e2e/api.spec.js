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
        firstName: 'Mingyuan',
        lastName: 'Xing'
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
    expect(result.name).toBe('Mingyuan Xing')
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

  test('isLoggedIn should return false when no token', async ({ page }) => {
    const result = await page.evaluate(() => {
      sessionStorage.clear()
      return window.NW.auth.isLoggedIn()
    })

    expect(result).toBe(false)
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

  test('isPro should return false for subscriber role', async ({ page }) => {
    const result = await page.evaluate(() => {
      window.NW.auth.save({
        token: 'abc123',
        userId: 1,
        role: 'subscriber'
      })

      return window.NW.auth.isPro()
    })

    expect(result).toBe(false)
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
      return await window.NW.login('mingyuan@example.com', 'Password123!')
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

  test('register should throw error when email already exists (409)', async ({ page }) => {
    await page.route('**/api/auth/register', async route => {
      await route.fulfill({
        status: 409,
        contentType: 'application/json',
        body: JSON.stringify({
          message: 'Email already in use'
        })
      })
    })

    const result = await page.evaluate(async () => {
      try {
        await window.NW.register(
          'Existing',
          'User',
          'existing@example.com',
          'Password123!',
          'subscriber',
          ''
        )
        return 'success'
      } catch (err) {
        return {
          message: err.message,
          status: err.status
        }
      }
    })

    expect(result.status).toBe(409)
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

  test('diary add should POST meal with authorization token', async ({ page }) => {
    await page.evaluate(() => {
      window.NW.auth.save({
        token: 'test-token',
        userId: 1,
        role: 'subscriber'
      })
    })

    let capturedBody = null

    await page.route('**/api/diary', async route => {
      const headers = route.request().headers()
      capturedBody = JSON.parse(route.request().postData())

      expect(headers.authorization).toBe('Bearer test-token')
      expect(route.request().method()).toBe('POST')

      await route.fulfill({
        status: 201,
        contentType: 'application/json',
        body: JSON.stringify({ id: 99, ...capturedBody })
      })
    })

    const result = await page.evaluate(async () => {
      return await window.NW.diary.add({
        date: '2026-04-20',
        mealType: 'LUNCH',
        foodName: 'Chicken Salad',
        kcal: 400,
        protein: 35,
        carbs: 20,
        fat: 10,
        sugar: 3
      })
    })

    expect(result.id).toBe(99)
    expect(capturedBody.foodName).toBe('Chicken Salad')
    expect(capturedBody.mealType).toBe('LUNCH')
  })

  test('diary remove should send DELETE request with authorization token', async ({ page }) => {
    await page.evaluate(() => {
      window.NW.auth.save({
        token: 'test-token',
        userId: 1,
        role: 'subscriber'
      })
    })

    const requestInfo = await page.evaluate(async () => {
      let method = null
      let auth = null
      const origFetch = window.fetch
      window.fetch = function (url, opts) {
        if (url.includes('/api/diary/')) {
          method = opts && opts.method
          auth = opts && opts.headers && opts.headers['Authorization']
          return Promise.resolve({
            status: 204,
            ok: true,
            json: () => Promise.resolve({})
          })
        }
        return origFetch(url, opts)
      }
      await window.NW.diary.remove(42)
      window.fetch = origFetch
      return { method, auth }
    })

    expect(requestInfo.method).toBe('DELETE')
    expect(requestInfo.auth).toBe('Bearer test-token')
  })

  test('diary get should throw error when server returns 500', async ({ page }) => {
    await page.evaluate(() => {
      window.NW.auth.save({
        token: 'test-token',
        userId: 1,
        role: 'subscriber'
      })
    })

    await page.route('**/api/diary?date=2026-04-21', async route => {
      await route.fulfill({
        status: 500,
        contentType: 'application/json',
        body: JSON.stringify({ message: 'Internal server error' })
      })
    })

    const result = await page.evaluate(async () => {
      try {
        await window.NW.diary.get('2026-04-21')
        return 'success'
      } catch (err) {
        return { status: err.status }
      }
    })

    expect(result.status).toBe(500)
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

  test('parseDiaryToMealLog should return empty meal groups for empty input', async ({ page }) => {
    const result = await page.evaluate(() => {
      return window.NW.parseDiaryToMealLog([])
    })

    expect(result.breakfast.length).toBe(0)
    expect(result.lunch.length).toBe(0)
    expect(result.dinner.length).toBe(0)
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

  test('parseRecipes should show all empty stars when averageRating is 0', async ({ page }) => {
    const result = await page.evaluate(() => {
      return window.NW.parseRecipes([
        {
          id: 20,
          name: 'Plain Rice',
          emoji: '🍚',
          tag: 'Lunch',
          kcal: 200,
          cost: '0.50',
          timeMin: 15,
          ingredients: ['Rice'],
          steps: ['Boil rice'],
          averageRating: 0,
          ratingCount: 0,
          commentCount: 0
        }
      ])
    })

    expect(result['20'].rating).toBe('★★★★☆')
  })

  test('parseRecipes should show all filled stars when averageRating is 5', async ({ page }) => {
    const result = await page.evaluate(() => {
      return window.NW.parseRecipes([
        {
          id: 30,
          name: 'Perfect Pasta',
          emoji: '🍝',
          tag: 'Dinner',
          kcal: 550,
          cost: '3.20',
          timeMin: 25,
          ingredients: ['Pasta', 'Sauce'],
          steps: ['Boil pasta', 'Add sauce'],
          averageRating: 5,
          ratingCount: 50,
          commentCount: 10
        }
      ])
    })

    expect(result['30'].rating).toBe('★★★★★')
  })
})
