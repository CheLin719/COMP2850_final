/**
 * NourishWell — API Client Layer
 * ══════════════════════════════════════════════════════════════
 * Centralised wrapper for all backend API calls.
 * Include this file at the very top of every page's <script> section:
 *   <script src="api.js"></script>
 *
 * Usage:
 *   await NW.login(email, password)   → { userId, token, role }
 *   await NW.diary.get('2026-04-20')  → { meals: [...] }
 *   NW.auth.token                     → current token string
 * ══════════════════════════════════════════════════════════════
 */

window.NW = (function () {
  // ── Base configuration ────────────────────────────────────────
  const BASE = '';

  // ── Token persistence (sessionStorage — cleared when tab is closed) ──
  const auth = {
    get token() { return sessionStorage.getItem('nw-token') || ''; },
    get userId() { return sessionStorage.getItem('nw-userId') || ''; },
    get role() { return sessionStorage.getItem('nw-role') || ''; },
    get name() { return sessionStorage.getItem('nw-name') || 'User'; },

    save(data) {
      if (data.token)  sessionStorage.setItem('nw-token',  data.token);
      if (data.userId) sessionStorage.setItem('nw-userId', String(data.userId));
      if (data.role)   sessionStorage.setItem('nw-role',   data.role);
      // name may come from the /me endpoint
      if (data.firstName) {
        sessionStorage.setItem('nw-name', (data.firstName + ' ' + (data.lastName || '')).trim());
      }
      if (data.name) sessionStorage.setItem('nw-name', data.name);
    },

    clear() {
      ['nw-token','nw-userId','nw-role','nw-name'].forEach(k => sessionStorage.removeItem(k));
    },

    isLoggedIn() { return !!this.token; },
    isPro() { return this.role === 'professional'; }
  };

  // ── Low-level request wrapper ─────────────────────────────────
  async function req(method, path, body = null, requiresAuth = true) {
    const headers = { 'Content-Type': 'application/json' };
    if (requiresAuth && auth.token) {
      headers['Authorization'] = 'Bearer ' + auth.token;
    }
    const opts = { method, headers };
    if (body !== null) opts.body = JSON.stringify(body);

    const res = await fetch(BASE + path, opts);

    // 204 No Content → return true directly
    if (res.status === 204) return true;

    // 401/403 → clear token and let the page handle it
    if (res.status === 401 || res.status === 403) {
      const err = new Error('AUTH_ERROR');
      err.status = res.status;
      throw err;
    }

    const data = await res.json().catch(() => ({}));

    if (!res.ok) {
      const err = new Error(data.message || data.error || 'Request failed');
      err.status = res.status;
      err.data = data;
      throw err;
    }

    return data;
  }

  // ══════════════════════════════════════════════════════════════
  // AUTH
  // ══════════════════════════════════════════════════════════════
  async function login(email, password) {
    const data = await req('POST', '/api/auth/login', { email, password }, false);
    auth.save(data);
    return data; // { userId, token, role }
  }

  async function register(firstName, lastName, email, password, role, licenceNo) {
    const body = { firstName, lastName, email, password, role };
    if (licenceNo) body.licenceNo = licenceNo;
    const data = await req('POST', '/api/auth/register', body, false);
    auth.save(data);
    return data; // { userId, token }
  }

  async function getMe() {
    const data = await req('GET', '/api/auth/me');
    auth.save(data);
    return data;
  }

  function logout() {
    auth.clear();
  }

  // ══════════════════════════════════════════════════════════════
  // DIARY
  // ══════════════════════════════════════════════════════════════
  const diary = {
    // GET /api/diary?date=YYYY-MM-DD → { meals: [...] }
    async get(date) {
      const qs = date ? '?date=' + date : '';
      return req('GET', '/api/diary' + qs);
    },

    // POST /api/diary → { id }
    async add(entry) {
      // entry: { date, mealType, foodName, kcal, protein, carbs, fat, sugar }
      return req('POST', '/api/diary', entry);
    },

    // DELETE /api/diary/:id → true
    async remove(id) {
      return req('DELETE', '/api/diary/' + id);
    }
  };

  // ══════════════════════════════════════════════════════════════
  // EXERCISE
  // ══════════════════════════════════════════════════════════════
  const exercise = {
    // GET /api/exercise?date=YYYY-MM-DD → [...]
    async get(date) {
      const qs = date ? '?date=' + date : '';
      return req('GET', '/api/exercise' + qs);
    },

    // POST /api/exercise → { id }
    async add(entry) {
      // entry: { date, activity, duration, kcal }
      return req('POST', '/api/exercise', entry);
    },

    // DELETE /api/exercise/:id → true
    async remove(id) {
      return req('DELETE', '/api/exercise/' + id);
    }
  };

  // ══════════════════════════════════════════════════════════════
  // RECIPES
  // ══════════════════════════════════════════════════════════════
  const recipes = {
    // GET /api/recipes → [...]
    async getAll() {
      return req('GET', '/api/recipes');
    },

    // GET /api/recipes/search?q=... → [...]
    async search(q) {
      return req('GET', '/api/recipes/search?q=' + encodeURIComponent(q));
    },

    // POST /api/recipes → { id }
    async add(recipe) {
      // recipe: { name, emoji, tag, kcal, cost, timeMin, ingredients[], steps[] }
      return req('POST', '/api/recipes', recipe);
    }
  };

  // ══════════════════════════════════════════════════════════════
  // FAVOURITES
  // ══════════════════════════════════════════════════════════════
  const favourites = {
    // GET /api/favourites → [{ recipeId, recipe }]
    async get() {
      return req('GET', '/api/favourites');
    },

    // POST /api/favourites → 201
    async add(recipeId) {
      return req('POST', '/api/favourites', { recipeId });
    },

    // DELETE /api/favourites/:recipeId → true
    async remove(recipeId) {
      return req('DELETE', '/api/favourites/' + recipeId);
    }
  };

  // ══════════════════════════════════════════════════════════════
  // RATINGS
  // ══════════════════════════════════════════════════════════════
  const ratings = {
    // POST /api/ratings (upsert) → 201
    async set(recipeId, score) {
      return req('POST', '/api/ratings', { recipeId, score });
    }
  };

  // ══════════════════════════════════════════════════════════════
  // COMMENTS
  // ══════════════════════════════════════════════════════════════
  const comments = {
    // GET /api/comments?recipeId=N → [{ id, user, text, createdAt }]
    async get(recipeId) {
      return req('GET', '/api/comments?recipeId=' + recipeId);
    },

    // POST /api/comments → { id }
    async add(recipeId, text) {
      return req('POST', '/api/comments', { recipeId, text });
    }
  };

  // ══════════════════════════════════════════════════════════════
  // PROFESSIONAL — CLIENTS
  // ══════════════════════════════════════════════════════════════
  const clients = {
    // GET /api/clients → [{ userId, name, email, stats }]
    async getAll() {
      return req('GET', '/api/clients');
    },

    // GET /api/clients/:id/diary?from=...&to=...
    async getDiary(clientId, from, to) {
      let qs = '';
      if (from && to) qs = '?from=' + from + '&to=' + to;
      else if (from)  qs = '?from=' + from;
      return req('GET', '/api/clients/' + clientId + '/diary' + qs);
    },

    // POST /api/clients/bind  { userId } → bind subscriber to current pro
    async bind(userId) {
      return req('POST', '/api/clients/bind', { userId: userId });
    },

    // DELETE /api/clients/:id/bind → unbind subscriber from current pro
    async unbind(userId) {
      return req('DELETE', '/api/clients/' + userId + '/bind');
    }
  };

  // ══════════════════════════════════════════════════════════════
  // MESSAGES
  // ══════════════════════════════════════════════════════════════
  const messages = {
    // GET /api/messages/:clientId → [{ id, senderId, text, createdAt }]
    async get(clientId) {
      return req('GET', '/api/messages/' + clientId);
    },

    // POST /api/messages → { id }
    async send(receiverId, text) {
      return req('POST', '/api/messages', { receiverId, text });
    }
  };

  // ══════════════════════════════════════════════════════════════
  // PLANS
  // ══════════════════════════════════════════════════════════════
  const plans = {
    // GET /api/plans/:clientId → [ApiPlanResponse]
    async get(clientId) {
      return req('GET', '/api/plans/' + clientId);
    },

    // PUT /api/plans/:clientId → ApiPlanResponse
    async save(clientId, data) {
      return req('PUT', '/api/plans/' + clientId, data);
    },

    // DELETE /api/plans/:planId → 204
    async delete(planId) {
      return req('DELETE', '/api/plans/' + planId);
    }
  };

  // ══════════════════════════════════════════════════════════════
  // NOTIFICATIONS
  // ══════════════════════════════════════════════════════════════
  const notifications = {
    // GET /api/notifications[?unreadOnly=true] → [ApiNotificationResponse]
    async getAll(unreadOnly) {
      const qs = unreadOnly ? '?unreadOnly=true' : '';
      return req('GET', '/api/notifications' + qs);
    },

    // GET /api/notifications/unread-count → { unreadCount: N }
    async getUnreadCount() {
      return req('GET', '/api/notifications/unread-count');
    },

    // PUT /api/notifications/:id/read → ApiNotificationResponse
    async markRead(notificationId) {
      return req('PUT', '/api/notifications/' + notificationId + '/read');
    },

    // PUT /api/notifications/read-all → { message }
    async markAllRead() {
      return req('PUT', '/api/notifications/read-all');
    },

    // DELETE /api/notifications/:id → { message }
    async delete(notificationId) {
      return req('DELETE', '/api/notifications/' + notificationId);
    }
  };

  // ══════════════════════════════════════════════════════════════
  // APPOINTMENTS
  // ══════════════════════════════════════════════════════════════
  const appointments = {
    // GET /api/appointments → [...]
    async getAll() {
      return req('GET', '/api/appointments');
    },

    // POST /api/appointments → { id }
    async create(clientId, date, time, type) {
      return req('POST', '/api/appointments', { clientId, date, time, type });
    },

    // PATCH /api/appointments/:id → 200
    async updateStatus(id, status) {
      return req('PATCH', '/api/appointments/' + id, { status });
    }
  };

  // ══════════════════════════════════════════════════════════════
  // Utility: map API mealType strings to frontend keys
  // ══════════════════════════════════════════════════════════════
  const MEAL_TYPE_MAP = {
    BREAKFAST:       'breakfast',
    MORNING_SNACK:   'morningSnack',
    LUNCH:           'lunch',
    AFTERNOON_SNACK: 'afternoonSnack',
    DINNER:          'dinner',
    EVENING_SNACK:   'eveningSnack',
    SNACK:           'morningSnack'  // fallback
  };

  const MEAL_KEY_TO_TYPE = {
    breakfast:      'BREAKFAST',
    morningSnack:   'MORNING_SNACK',
    lunch:          'LUNCH',
    afternoonSnack: 'AFTERNOON_SNACK',
    dinner:         'DINNER',
    eveningSnack:   'EVENING_SNACK'
  };

  function mealTypeToKey(apiType) {
    return MEAL_TYPE_MAP[apiType] || 'breakfast';
  }

  function keyToMealType(key) {
    return MEAL_KEY_TO_TYPE[key] || 'BREAKFAST';
  }

  // ══════════════════════════════════════════════════════════════
  // Convert backend diary meals array to frontend mealLog structure
  // ══════════════════════════════════════════════════════════════
  function parseDiaryToMealLog(apiMeals) {
    const log = {
      breakfast: [], morningSnack: [], lunch: [],
      afternoonSnack: [], dinner: [], eveningSnack: []
    };
    if (!apiMeals) return log;
    apiMeals.forEach(m => {
      const key = mealTypeToKey(m.mealType);
      log[key].push({
        _id:     m.id,          // retain backend id for deletion
        name:    m.foodName,
        kcal:    m.kcal,
        time:    m.time || '',
        protein: m.protein || 0,
        carbs:   m.carbs   || 0,
        fat:     m.fat     || 0,
        sugar:   m.sugar   || 0
      });
    });
    return log;
  }

  // ══════════════════════════════════════════════════════════════
  // Convert backend recipes array to frontend RECIPES object format
  // ══════════════════════════════════════════════════════════════
  function parseRecipes(apiRecipes) {
    const result = {};
    if (!Array.isArray(apiRecipes)) return result;
    apiRecipes.forEach(r => {
      const key = String(r.id);
      result[key] = {
        id:          key,
        _numericId:  r.id,       // retain numeric id for API calls
        name:        r.name,
        emoji:       r.emoji || '🍽️',
        tag:         r.tag || '',
        kcal:        r.kcal || 0,
        cost:        '£' + (r.cost || '0.00'),
        timeMin:     r.timeMin || 0,
        ingredients: r.ingredients || [],
        steps:       r.steps || [],
        bg:          r.bgColor || '#e8f4f2',
        rating:      r.averageRating
          ? '★'.repeat(Math.round(r.averageRating)) + '☆'.repeat(5 - Math.round(r.averageRating))
          : '★★★★☆',
        averageRating: r.averageRating || 0,
        ratingCount:   r.ratingCount   || 0,
        commentCount:  r.commentCount  || 0
      };
    });
    return result;
  }

  // ══════════════════════════════════════════════════════════════
  // Public API
  // ══════════════════════════════════════════════════════════════
  return {
    auth,
    login,
    register,
    getMe,
    logout,
    diary,
    exercise,
    recipes,
    favourites,
    ratings,
    comments,
    clients,
    messages,
    plans,
    notifications,
    appointments,
    // utilities
    mealTypeToKey,
    keyToMealType,
    parseDiaryToMealLog,
    parseRecipes
  };
})();
