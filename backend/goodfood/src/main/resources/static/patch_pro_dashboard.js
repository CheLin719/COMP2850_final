/**
 * NourishWell — pro_dashboard.html API Patch
 * ══════════════════════════════════════════════════════════════
 * Overrides:
 *  - initProDashboard: fetch clients from API, match to sidebar (static + bind)
 *  - selectClient: load diary/messages/appointments from API
 *  - sendMessage: POST to /api/messages
 *  - confirmAppt: POST to /api/appointments
 *  - toggleApptStatus: PATCH /api/appointments/:id
 * ══════════════════════════════════════════════════════════════
 */

// ── 0. Auth guard ──
(async function () {
  if (!NW.auth.isLoggedIn()) { window.location.replace('index.html'); return; }
  if (NW.auth.role && NW.auth.role !== 'professional') { window.location.replace('dashboard.html'); return; }
  if (!NW.auth.role) {
    try {
      var me = await NW.getMe();
      if (me && me.role !== 'professional') { window.location.replace('dashboard.html'); return; }
    } catch (e) { console.warn('[NW] auth check skipped:', e.message); }
  }
  document.body.style.opacity = '1';
  document.body.style.transition = 'opacity 0.2s';
})();

/** API client cache: backend userId → client object */
var _apiClients = {};

/** Copy profile fields from API client response into the local client object */
function _applyProfileToClient(c, apiClient) {
  if (!c || !apiClient) return;
  if (apiClient.height)     c.height          = apiClient.height;
  if (apiClient.weight)     c.weight          = apiClient.weight;
  if (apiClient.age)        c.age             = apiClient.age;
  if (apiClient.goal)       { c.goal = apiClient.goal; if (c.stats) c.stats.goal = apiClient.goal; }
  if (apiClient.targetKcal) c.target_kcal     = apiClient.targetKcal;
}

// ══════════════════════════════════════════════════════════════
// 1. Init: fetch client list, map to frontend clients object
// ══════════════════════════════════════════════════════════════
/** Fetch client list from API and update sidebar + overview */
async function initProDashboard() {
  try {
    var list = await NW.clients.getAll();

    list.forEach(function(client) {
      var id = String(client.userId);
      _apiClients[id] = client;

      // Try to match by name to static sidebar clients (rose, james, etc.)
      var matchKey = Object.keys(clients).find(function(k) {
        return clients[k].name && clients[k].name.toLowerCase() === (client.name || '').toLowerCase();
      });
      if (matchKey) {
        clients[matchKey]._apiId = id;
        clients[matchKey]._apiClient = client;
      }

      // Also match bind_* clients (dynamically added via acceptBind)
      var bindKey = Object.keys(clients).find(function(k) {
        return k.indexOf('bind_') === 0 && clients[k]._apiId === id;
      });
      if (bindKey) {
        clients[bindKey]._apiClient = client;
        if (client.name) clients[bindKey].name = client.name;
        // Apply profile data immediately so BMI/targets show before diary loads
        _applyProfileToClient(clients[bindKey], client);
      }

      // Apply profile to static client match too
      if (matchKey) {
        _applyProfileToClient(clients[matchKey], client);
      }

      // If no match at all but this is a bound client, create entry
      if (!matchKey && !bindKey) {
        // Check if this user is in nw-pro-accepted-clients
        try {
          var accepted = JSON.parse(localStorage.getItem('nw-pro-accepted-clients') || '[]');
          var found = accepted.find(function(a) { return String(a.userId) === id; });
          if (found) {
            var newKey = 'bind_' + id;
            if (!clients[newKey]) {
              var ini = (client.name || '').split(' ').map(function(w) { return (w[0] || '').toUpperCase(); }).join('').substring(0, 2) || '??';
              clients[newKey] = {
                name: client.name || 'Client',
                initials: ini,
                color: '#7c3aed',
                stats: { goal: found.goal || 'General', avg_kcal: 0, protein: '—', exercise_days: 0, bmi: '—', score: '—', status: 'Active' },
                calorie_trend: [],
                messages: [],
                appointments: [],
                _apiId: id,
                _apiClient: client
              };
            }
          }
        } catch (e) {}
      }
    });

    // Update sidebar data-api-id attributes
    document.querySelectorAll('.sn-item[data-client]').forEach(function(item) {
      var localKey = item.getAttribute('data-client');
      if (clients[localKey] && clients[localKey]._apiId) {
        item.setAttribute('data-api-id', clients[localKey]._apiId);
      }
    });

    // ── Final pass: re-apply profile to any bind_* clients that now exist ──
    // (handles case where sidebar rebuilt AFTER the per-item loop above)
    list.forEach(function(client) {
      var id = String(client.userId);
      var forceKey = 'bind_' + id;
      if (clients[forceKey]) {
        clients[forceKey]._apiId = id;
        clients[forceKey]._apiClient = client;
        _applyProfileToClient(clients[forceKey], client);
      }
    });

    _updateOverviewStats(list);

    // ── Purge stale localStorage entries not present in backend response ──
    var liveIds = list.map(function(c) { return String(c.userId); });
    try {
      var accepted = JSON.parse(localStorage.getItem('nw-pro-accepted-clients') || '[]');
      var cleaned = accepted.filter(function(a) { return liveIds.indexOf(String(a.userId)) !== -1; });
      if (cleaned.length !== accepted.length) {
        localStorage.setItem('nw-pro-accepted-clients', JSON.stringify(cleaned));
        // Remove stale sidebar items
        document.querySelectorAll('.sn-item[data-client]').forEach(function(el) {
          var key = el.getAttribute('data-client');
          if (!key || key.indexOf('bind_') !== 0) return;
          var id = key.replace('bind_', '');
          if (liveIds.indexOf(id) === -1) {
            el.remove();
            delete clients[key];
          }
        });
      }
    } catch(e) {}

    // Also purge stale bind requests
    try {
      var reqs = JSON.parse(localStorage.getItem('nw-bind-requests') || '[]');
      var reqsCleaned = reqs.filter(function(r) {
        // Keep pending requests (user may not have been accepted yet) and live accepted ones
        if (r.status === 'pending') return true;
        return liveIds.indexOf(String(r.userId)) !== -1;
      });
      if (reqsCleaned.length !== reqs.length) {
        localStorage.setItem('nw-bind-requests', JSON.stringify(reqsCleaned));
      }
    } catch(e) {}

  } catch (e) {
    console.warn('[NW] initProDashboard failed:', e.message);
  }
}

/** Append API-bound clients to the Client Snapshot table (#snapshotTable).
 *  Static rows (Rose, James, …) are kept; only missing bind_* clients are added. */
function _updateOverviewStats(list) {
  var tbody = document.querySelector('#snapshotTable');
  if (!tbody) return;
  var colors = ['#1a7a6a', '#7aaad4', '#b8621f', '#7c3aed', '#d4956a', '#b5413e'];
  list.forEach(function (ac, idx) {
    var clientKey = 'bind_' + ac.userId;
    // Skip if this client's row already exists (prevents duplicates on repeated calls)
    if (tbody.querySelector('[data-client-key="' + clientKey + '"]')) return;
    var s = ac.stats || {};
    var ini = (ac.name || '').split(' ')
      .map(function (w) { return (w[0] || '').toUpperCase(); }).join('').substring(0, 2) || '??';
    var color = (clients[clientKey] && clients[clientKey].color) || colors[idx % colors.length];
    var goal  = ac.goal || '—';
    var meta  = [ac.weight ? ac.weight + ' kg' : '', ac.age ? '· ' + ac.age : ''].filter(Boolean).join(' ');
    var statusBg, statusColor, statusText;
    if (s.status === 'active') {
      statusBg = 'var(--teal-lll)'; statusColor = 'var(--teal)'; statusText = '✓ Active';
    } else {
      statusBg = 'var(--amber-ll)'; statusColor = 'var(--amber)'; statusText = '⚡ Inactive';
    }
    var row = '<tr data-client-key="' + clientKey + '" tabindex="0" role="button"'
      + ' onclick="selectClientFromTable(\'' + clientKey + '\')"'
      + ' style="cursor:pointer;transition:background .15s"'
      + ' onmouseover="this.style.background=\'var(--paper)\'"'
      + ' onmouseout="this.style.background=\'transparent\'">'
      // Col 1: avatar + name + meta
      + '<td style="padding:12px 0"><div style="display:flex;align-items:center;gap:10px">'
      + '<div style="width:32px;height:32px;border-radius:50%;background:' + color
      + ';color:#fff;display:flex;align-items:center;justify-content:center;font-size:11px;font-weight:700">'
      + escapeHtml(ini) + '</div>'
      + '<div><div style="font-size:13px;font-weight:600;color:var(--ink)">' + escapeHtml(ac.name) + '</div>'
      + (meta ? '<div style="font-family:\'JetBrains Mono\',monospace;font-size:9px;color:var(--ink-f)">' + escapeHtml(meta) + '</div>' : '')
      + '</div></div></td>'
      // Col 2: goal
      + '<td style="text-align:center;font-family:\'JetBrains Mono\',monospace;font-size:10px;color:var(--ink-m)">'
      + escapeHtml(goal) + '</td>'
      // Col 3: avg kcal — not in getAll(); show diary count instead
      + '<td style="text-align:center;font-family:\'JetBrains Mono\',monospace;font-size:11px;font-weight:600;color:var(--ink-f)">'
      + (s.diaryCount || 0) + ' logs</td>'
      // Col 4: protein — not in getAll()
      + '<td style="text-align:center"><span style="font-family:\'JetBrains Mono\',monospace;font-size:10px;'
      + 'padding:2px 8px;border-radius:8px;background:var(--paper);color:var(--ink-f)">—</span></td>'
      // Col 5: exercise — not in getAll(); show last diary date
      + '<td style="text-align:center"><div style="font-family:\'JetBrains Mono\',monospace;font-size:10px;color:var(--ink-m)">'
      + (s.lastDiaryDate || '—') + '</div></td>'
      // Col 6: status
      + '<td style="text-align:center"><span style="font-family:\'JetBrains Mono\',monospace;font-size:9px;'
      + 'padding:3px 10px;border-radius:8px;background:' + statusBg + ';color:' + statusColor + '">'
      + statusText + '</span></td>'
      + '</tr>';
    tbody.insertAdjacentHTML('beforeend', row);
  });
}

// ══════════════════════════════════════════════════════════════
// 2. Override selectClient: fetch data from API
// ══════════════════════════════════════════════════════════════
var _origSelectClient = window.selectClient;
/** Load client data from API when switching client view */
window.selectClient = async function (id, el) {
  if (id === 'overview') {
    if (_origSelectClient) _origSelectClient(id, el);
    return;
  }

  // Ensure clients[id] has minimum required fields so original render won't crash
  if (!clients[id] && id.indexOf('bind_') === 0) {
    clients[id] = {
      name: 'Client', initials: '??', color: '#7c3aed',
      stats: { goal: 'General', avg_kcal: 0, protein: 0, exercise_days: 0, streak: 0, status: 'New' },
      calorie_trend: [0,0,0,0,0,0,0],
      messages: [], appointments: [],
      _apiId: id.replace('bind_', '')
    };
  }

  // Execute original UI switch (renders with whatever data is already in clients[id])
  if (_origSelectClient) _origSelectClient(id, el);

  var c0 = clients[id];
  if (c0) {
    // Seed defaults so score ring and exercise chart are never blank before async loads
    if (!c0.scores)   c0.scores   = { nutrition: 0, consistency: 0, hydration: 70, exercise: 0 };
    if (!c0.exercise) c0.exercise = [0, 0, 0, 0, 0, 0, 0];
    // Re-render panels with any profile data already applied by initProDashboard
    if (typeof renderBMI           === 'function') renderBMI(c0);
    if (typeof renderStats         === 'function') renderStats(c0);
    if (typeof renderClientHeader  === 'function') renderClientHeader(c0);
    if (typeof renderScoreRing     === 'function') renderScoreRing(c0);
    if (typeof renderExerciseChart === 'function') renderExerciseChart(c0);
  }

  // Get API id — works for both static clients and bind_* clients
  var apiId = clients[id] ? clients[id]._apiId : null;
  if (!apiId) return;

  var today = new Date().toISOString().split('T')[0];
  var monthAgo = new Date(Date.now() - 7 * 24 * 3600 * 1000).toISOString().split('T')[0];

  // Capture the client object reference BEFORE awaiting, so that a concurrent
  // DOMContentLoaded replacement of clients[id] during the await cannot swap in
  // a stale object and cause the BMI/weight to flash to old values.
  var c = clients[id];

  var results = await Promise.allSettled([
    NW.messages.get(apiId),
    NW.appointments.getAll(),
    NW.clients.getDiary(apiId, monthAgo, today),
    NW.plans.get(apiId)
  ]);

  var messagesRes    = results[0];
  var appointmentsRes = results[1];
  var diaryRes       = results[2];
  var plansRes       = results[3];

  // ── Messages ──
  if (messagesRes.status === 'fulfilled') {
    var msgs = messagesRes.value || [];
    c.messages = msgs.map(function(m) {
      return {
        from: String(m.senderId) === apiId ? 'client' : 'pro',
        text: m.text,
        time: m.createdAt ? new Date(m.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '',
        _id: m.id
      };
    });
    // Always sync to mailMessages so API clients appear in the mail panel
    if (window.mailMessages !== undefined) {
      var clientMsgs = c.messages
        .filter(function(m) { return m.from === 'client'; })
        .map(function(m) { return { from: id, text: m.text, time: m.time, unread: false }; });
      if (clientMsgs.length > 0) {
        window.mailMessages[id] = clientMsgs;
        if (window.clientColors) window.clientColors[id] = (c.color) || '#7c3aed';
        if (window.clientNames) window.clientNames[id] = c.name || 'Client';
        if (typeof updateMailDot === 'function') updateMailDot();
      }
    }
    if (typeof renderChat === 'function') renderChat(c);
  }

  // ── Appointments ──
  if (appointmentsRes.status === 'fulfilled') {
    var all = appointmentsRes.value || [];
    var mine = all.filter(function(a) { return String(a.clientId) === apiId; });
    c._appointments_api = mine;
    c.appointments = mine.map(function(a) {
      return { _id: a.id, time: a.time, date: a.date, type: a.type || 'Check-in', status: a.status || 'pending' };
    });
    if (typeof renderAppointments === 'function') renderAppointments(c);
  }

  // ── Plans (targets + weekly schedule) ──
  if (plansRes.status === 'fulfilled') {
    var planList = plansRes.value || [];
    planList.forEach(function(p) {
      try {
        var days = JSON.parse(p.daysJson || '[]');
        if (p.planType === 'meal') {
          c.meals = days.length === 7 ? days : c.meals;
          if (p.targetKcal)    c.target_kcal    = p.targetKcal;
          if (p.targetProtein) c.target_protein = p.targetProtein;
          if (p.targetCarbsPct) c._targetCarbsPct = p.targetCarbsPct;
          if (p.targetFatPct)   c._targetFatPct  = p.targetFatPct;
          if (p.notes) c.notes = p.notes;
        } else if (p.planType === 'training') {
          c.training = days.length === 7 ? days : c.training;
        }
      } catch(e) {}
    });
    if (typeof renderPlan === 'function') renderPlan(c);
  }

  // ── Diary summary + profile ──
  if (diaryRes.status === 'fulfilled') {
    var data = diaryRes.value || {};
    var mealEntries = data.meals || [];
    var summary = data.summary;
    var profile = data.profile;

    // ── 1. Profile → height / weight / age / goal ──
    if (profile) {
      if (profile.height)     c.height          = profile.height;
      if (profile.weight)     c.weight          = profile.weight;
      if (profile.age)        c.age             = profile.age;
      if (profile.goal)       { c.goal = profile.goal; if (!c.stats) c.stats = {}; c.stats.goal = profile.goal; }
      if (profile.targetKcal) c.target_kcal = profile.targetKcal;
    }

    // ── 2. Stats: averages over logged days ──
    if (!c.stats) c.stats = {};
    if (summary) {
      var loggedDays = new Set(mealEntries.map(function(m){ return m.date; })).size || 1;
      if (typeof summary.totalCalories === 'number') c.stats.avg_kcal = Math.round(summary.totalCalories / loggedDays);
      if (typeof summary.totalProtein  === 'number') c.stats.protein  = Math.round(summary.totalProtein  / loggedDays);
      if (typeof summary.totalExerciseEntries === 'number') c.stats.exercise_days = summary.totalExerciseEntries;
    }

    // ── 3. Streak: consecutive days ending today ──
    var dateSets = new Set(mealEntries.map(function(m){ return m.date; }));
    var streak = 0;
    for (var si = 0; si < 60; si++) {
      var d = new Date(Date.now() - si * 86400000).toISOString().split('T')[0];
      if (dateSets.has(d)) streak++;
      else if (si > 0) break;
    }
    c.stats.streak = streak;

    // ── 4. Calorie trend — always rebuild so chart reflects actual data ──
    _buildCalorieTrend(c, mealEntries);
    if (typeof renderCalorieTrend === 'function') renderCalorieTrend(c);

    // ── 5. Exercise frequency (weekly kcal per day Mon–Sun) ──
    var weekKcal = [0, 0, 0, 0, 0, 0, 0];
    (data.exercise || []).forEach(function(ex) {
      var dow = (new Date(ex.date).getDay() + 6) % 7;
      weekKcal[dow] += ex.kcal || 0;
    });
    // Always update so chart clears stale data when switching clients
    c.exercise = weekKcal;
    if (typeof renderExerciseChart === 'function') renderExerciseChart(c);

    // ── 6. Macros: use actual sums from diary entries ──
    var loggedDays  = new Set(mealEntries.map(function(m){ return m.date; })).size || 1;
    var avgKcal     = c.stats.avg_kcal || 0;
    var targetKcal  = c.target_kcal  || 2000;
    var targetProt  = c.target_protein || Math.round(targetKcal * 0.30 / 4);
    var carbsPct    = c._targetCarbsPct || 45;
    var fatPct      = c._targetFatPct   || 25;
    var targetCarbs = Math.round(targetKcal * (carbsPct / 100) / 4);
    var targetFat   = Math.round(targetKcal * (fatPct  / 100) / 9);
    var targetSugar = Math.round(targetKcal * 0.05 / 4);

    var actProtein = c.stats.protein || 0;
    var actSugar   = summary ? Math.round((summary.totalSugar || 0) / loggedDays) : 0;
    var totalCarbs = 0, totalFat = 0;
    mealEntries.forEach(function(m) { totalCarbs += m.carbs || 0; totalFat += m.fat || 0; });
    var actCarbs = Math.round(totalCarbs / loggedDays);
    var actFat   = Math.round(totalFat   / loggedDays);

    // Foods logged via the quick picker are stored with macros=0 (only kcal is sent).
    // Fall back to the same fixed-ratio estimation the user dashboard uses in updateDonut,
    // so the expert sees non-zero values consistent with the user's own macro display.
    if (actCarbs === 0 && actProtein === 0 && actFat === 0 && actSugar === 0 && avgKcal > 0) {
      actCarbs   = Math.round(avgKcal * 0.42 / 4);
      actProtein = Math.round(avgKcal * 0.28 / 4);
      actFat     = Math.round(avgKcal * 0.18 / 9);
      actSugar   = Math.round(avgKcal * 0.12 / 4);
    }

    if (avgKcal > 0 || actProtein > 0 || actCarbs > 0 || actFat > 0) {
      c.macros = {
        carbs:   { actual: actCarbs,   target: targetCarbs, color: '#2f8f7f' },
        protein: { actual: actProtein, target: targetProt,  color: '#d4956a' },
        fat:     { actual: actFat,     target: targetFat,   color: '#7aaad4' },
        sugar:   { actual: actSugar,   target: targetSugar, color: '#e07b78' }
      };
    }

    // ── 7. Health scores (0–100) ──
    var exDays  = c.stats.exercise_days || 0;
    var kcalPct = targetKcal > 0 ? (avgKcal / targetKcal) : 0;
    var nutritionScore  = avgKcal > 0 ? Math.round(Math.max(0, 100 - Math.abs(1 - kcalPct) * 120)) : 0;
    var consistencyScore = Math.min(100, Math.round((streak / 30) * 100));
    var exerciseScore    = Math.min(100, Math.round((exDays / 5) * 100));
    var hydrationScore   = 70; // no hydration data available
    c.scores = {
      nutrition:   nutritionScore,
      consistency: consistencyScore,
      hydration:   hydrationScore,
      exercise:    exerciseScore
    };

    // ── 8. Re-render all analytics panels ──
    if (typeof renderStats        === 'function') renderStats(c);
    if (typeof renderBMI          === 'function') renderBMI(c);
    if (typeof renderMacros       === 'function') renderMacros(c);
    if (typeof renderScoreRing    === 'function') renderScoreRing(c);
    if (typeof renderClientHeader === 'function') renderClientHeader(c);

    // ── 9. Sync back to clients[id] in case the DOMContentLoaded handler
    //       (pro_dashboard.html ~line 2455) replaced the object during the await.
    //       Without this, re-clicking the client would re-read the zeroed copy.
    var _liveRef = clients[id];
    if (_liveRef && _liveRef !== c) {
      _liveRef.stats         = c.stats;
      _liveRef.weight        = c.weight;
      _liveRef.height        = c.height;
      _liveRef.age           = c.age;
      _liveRef.goal          = c.goal;
      _liveRef.target_kcal   = c.target_kcal;
      _liveRef.calorie_trend = c.calorie_trend;
      _liveRef.exercise      = c.exercise;
      _liveRef.scores        = c.scores;
      _liveRef.macros        = c.macros;
    }

  } else {
    // Diary fetch failed (e.g. binding not yet confirmed in backend).
    // Still render panels with defaults so the page isn't blank.
    // Use the same c reference captured before the await — do NOT re-read clients[id].
    if (c) {
      if (!c.scores)   c.scores   = { nutrition: 0, consistency: 0, hydration: 70, exercise: 0 };
      if (!c.exercise) c.exercise = [0, 0, 0, 0, 0, 0, 0];
      if (typeof renderStats         === 'function') renderStats(c);
      if (typeof renderBMI           === 'function') renderBMI(c);
      if (typeof renderScoreRing     === 'function') renderScoreRing(c);
      if (typeof renderExerciseChart === 'function') renderExerciseChart(c);
      if (typeof renderCalorieTrend  === 'function') renderCalorieTrend(c);
    }
  }
};

/** Build 7-day calorie trend array from API diary data */
function _buildCalorieTrend(c, meals) {
  var kcalByDate = {};
  meals.forEach(function(m) { kcalByDate[m.date] = (kcalByDate[m.date] || 0) + (m.kcal || 0); });
  var trend = [];
  for (var i = 6; i >= 0; i--) {
    var d = new Date(Date.now() - i * 24 * 3600 * 1000).toISOString().split('T')[0];
    trend.push(kcalByDate[d] || 0);
  }
  c.calorie_trend = trend;
}

// ══════════════════════════════════════════════════════════════
// 3. Override sendMessage: POST to API
// ══════════════════════════════════════════════════════════════
var _origSendMessage = window.sendMessage;
/** Send message to client via API with optimistic UI update */
window.sendMessage = async function () {
  var box = document.getElementById('chatInputBox');
  var text = (box ? box.value : '').trim();
  if (!text) return;

  var apiId = currentClient && clients[currentClient] ? clients[currentClient]._apiId : null;
  if (!apiId) {
    if (_origSendMessage) _origSendMessage();
    return;
  }

  var c = clients[currentClient];
  c.messages.push({ from: 'pro', text: text, time: 'Just now', _id: null });
  if (typeof renderChat === 'function') renderChat(c);
  if (box) box.value = '';
  var wrap = document.getElementById('chatMessages');
  if (wrap) wrap.scrollTop = wrap.scrollHeight;

  try {
    var res = await NW.messages.send(apiId, text);
    var entry = c.messages.find(function(m) { return m.text === text && !m._id; });
    if (entry && res && res.id) entry._id = res.id;
  } catch (e) {
    console.warn('[NW] sendMessage failed:', e.message);
    if (typeof proToast === 'function') proToast('Failed to send message', '#dc2626');
    c.messages.pop();
    if (typeof renderChat === 'function') renderChat(c);
  }
};

// ══════════════════════════════════════════════════════════════
// 4. Override confirmAppt: POST to API
// ══════════════════════════════════════════════════════════════
var _origConfirmAppt = window.confirmAppt;
/** Book appointment via API with optimistic UI update */
window.confirmAppt = async function () {
  if (!currentClient) { closeApptModal(); return; }
  var date = document.getElementById('apptDate').value;
  var time = document.getElementById('apptTime').value;
  var type = document.getElementById('apptType').value;
  if (!date || !time) { proToast('Please fill in date and time', '#dc2626'); return; }

  var apiId = clients[currentClient] ? clients[currentClient]._apiId : null;
  if (!apiId) { if (_origConfirmAppt) _origConfirmAppt(); return; }

  var d = new Date(date);
  var dateStr = d.getDate() + ' ' + ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'][d.getMonth()];
  var c = clients[currentClient];
  var newAppt = { time: time, date: dateStr, type: type, status: 'pending', _id: null };
  c.appointments.unshift(newAppt);
  closeApptModal();
  if (typeof renderAppointments === 'function') renderAppointments(c);
  proToast('✓ Appointment booked', '#1e6b5e');

  try {
    var res = await NW.appointments.create(apiId, date, time, type);
    if (res && res.id) newAppt._id = res.id;
  } catch (e) {
    console.warn('[NW] confirmAppt failed:', e.message);
    proToast('Booking saved locally only', '#b8621f');
  }
};

// ══════════════════════════════════════════════════════════════
// 5. Override toggleApptStatus: PATCH API
// ══════════════════════════════════════════════════════════════
var _origToggleApptStatus = window.toggleApptStatus;
/** Toggle appointment confirmed/pending status via API */
window.toggleApptStatus = async function (idx) {
  if (!currentClient) return;
  var appts = clients[currentClient].appointments;
  var appt = appts[idx];
  if (!appt) return;

  appt.status = appt.status === 'confirmed' ? 'pending' : 'confirmed';
  if (typeof renderAppointments === 'function') renderAppointments(clients[currentClient]);
  proToast(appt.status === 'confirmed' ? '✓ Confirmed' : 'Marked as pending');

  if (appt._id) {
    try {
      await NW.appointments.updateStatus(appt._id, appt.status);
    } catch (e) {
      appt.status = appt.status === 'confirmed' ? 'pending' : 'confirmed';
      if (typeof renderAppointments === 'function') renderAppointments(clients[currentClient]);
    }
  }
};

// ══════════════════════════════════════════════════════════════
// 6. Remove client (pro initiates unbind)
// ══════════════════════════════════════════════════════════════
window._proRemoveClient = async function(clientKey) {
  if (!confirm('Remove this client from your list? They will be notified.')) return;
  var c = clients[clientKey];
  if (!c) return;
  var apiId = c._apiId;

  if (apiId) {
    try {
      await NW.clients.unbind(apiId);
    } catch(e) {
      // 404 means user no longer exists in DB — stale data, still clean up locally
      if (!e || e.status !== 404) {
        proToast('Remove failed: ' + (e && e.message || 'unknown'), '#dc2626');
        return;
      }
    }
  }

  // Remove from localStorage accepted list
  try {
    var accepted = JSON.parse(localStorage.getItem('nw-pro-accepted-clients') || '[]');
    accepted = accepted.filter(function(a) { return String(a.userId) !== String(apiId); });
    localStorage.setItem('nw-pro-accepted-clients', JSON.stringify(accepted));
  } catch(e) {}

  // Remove from bind requests
  try {
    var reqs = JSON.parse(localStorage.getItem('nw-bind-requests') || '[]');
    reqs = reqs.filter(function(r) { return String(r.userId) !== String(apiId); });
    localStorage.setItem('nw-bind-requests', JSON.stringify(reqs));
  } catch(e) {}

  // Remove sidebar entry
  var sideEl = document.querySelector('.sn-item[data-client="' + clientKey + '"]');
  if (sideEl) sideEl.remove();

  // Remove from clients object
  delete clients[clientKey];

  // Navigate to overview
  currentClient = null;
  var overview = document.getElementById('page-overview');
  if (overview) {
    document.querySelectorAll('.page').forEach(function(p){ p.classList.remove('active'); });
    overview.classList.add('active');
  }
  proToast('Client removed', '#1e6b5e');
};

// Patch renderClientHeader to add Remove button for API-backed clients
var _origRenderClientHeader = window.renderClientHeader;
window.renderClientHeader = function(c) {
  if (_origRenderClientHeader) _origRenderClientHeader(c);
  // Add remove button only for bound (non-static) clients that have a backend link
  if (!c._apiId) return;
  var staticKeys = ['rose', 'james', 'sara', 'mike', 'priya'];
  var isStatic = staticKeys.some(function(k) { return clients[k] === c; });
  if (isStatic) return;
  var header = document.getElementById('clientHeader');
  if (!header) return;
  var actionsDiv = header.querySelector('.ch-actions');
  if (!actionsDiv) return;
  // Avoid double-adding
  if (actionsDiv.querySelector('.ch-btn-remove')) return;
  var clientKey = Object.keys(clients).find(function(k) { return clients[k] === c; });
  if (!clientKey) return;
  var btn = document.createElement('button');
  btn.className = 'ch-btn ch-btn-remove';
  btn.style.cssText = 'color:#dc2626;border-color:#dc2626';
  btn.textContent = '🗑 Remove Client';
  btn.onclick = function() { window._proRemoveClient(clientKey); };
  actionsDiv.appendChild(btn);
};

// ══════════════════════════════════════════════════════════════
// 7. Poll for user-unbound notifications (pro side)
// ══════════════════════════════════════════════════════════════
(function _startProNotifPoll() {
  var _lastNotifCheck = 0;
  async function _checkProNotifs() {
    if (!NW.auth.isLoggedIn()) return;
    try {
      var data = await NW.notifications.getUnreadCount();
      var count = (data && data.unreadCount) || 0;
      var dot = document.getElementById('mailDot');
      if (dot) dot.style.display = count > 0 ? 'block' : 'none';

      if (count > 0) {
        var notifs = await NW.notifications.getAll(true);
        if (!Array.isArray(notifs)) return;
        for (var _ni = 0; _ni < notifs.length; _ni++) {
          var _n = notifs[_ni];
          if (_n.type === 'user_unbound') {
            await NW.notifications.markRead(_n.id);
            // Find which client unbound by matching relatedId (clientId) to sidebar
            var unbound = _n.relatedId ? String(_n.relatedId) : null;
            var unboundKey = null;
            if (unbound) {
              unboundKey = Object.keys(clients).find(function(k) {
                return clients[k]._apiId === unbound;
              });
            }
            var clientName = (unboundKey && clients[unboundKey]) ? clients[unboundKey].name : (_n.message || 'A client');
            var doDelete = window.confirm(
              clientName + ' has disconnected from you.\n\nWould you like to remove them from your client list?'
            );
            if (doDelete && unboundKey) {
              window._proRemoveClient(unboundKey);
            } else {
              proToast('⚠️ ' + clientName + ' disconnected', '#b8621f');
              initProDashboard();
            }
          }
        }
      }
    } catch(e) { /* ignore */ }
  }
  document.addEventListener('DOMContentLoaded', function() {
    setTimeout(_checkProNotifs, 2000);
    setInterval(_checkProNotifs, 30000);
  });
})();

document.addEventListener('DOMContentLoaded', function () {
  document.querySelectorAll('a.logout').forEach(function(el) {
    el.addEventListener('click', function (e) {
      e.preventDefault();
      NW.logout();
      window.location.href = 'index.html';
    });
  });
});

// ══════════════════════════════════════════════════════════════
// 8. Override mail panel to include API-bound clients
// ══════════════════════════════════════════════════════════════
var _origOpenMailPanel = window.openMailPanel;
window.openMailPanel = function() {
  // Inject all API-bound clients into mailMessages / clientColors / clientNames
  Object.keys(clients).forEach(function(k) {
    var c = clients[k];
    if (!c || !c._apiId) return;
    // Register name/color so the mail panel can render them
    clientColors[k] = c.color || '#7c3aed';
    clientNames[k]  = c.name  || 'Client';
    // Use loaded client messages if available, otherwise a placeholder entry
    var clientMsgs = (c.messages || [])
      .filter(function(m) { return m.from === 'client'; })
      .map(function(m) { return { from: k, text: m.text, time: m.time, unread: false }; });
    if (clientMsgs.length === 0) {
      clientMsgs = [{ from: k, text: 'No messages yet', time: '', unread: false }];
    }
    mailMessages[k] = clientMsgs;
  });
  if (_origOpenMailPanel) _origOpenMailPanel();
};

var _origSelectClientFromMail = window.selectClientFromMail;
window.selectClientFromMail = function(id) {
  var staticKeys = ['overview', 'rose', 'james', 'sara', 'mike', 'priya'];
  if (staticKeys.indexOf(id) === -1) {
    // API-bound client key (e.g. bind_123)
    if (typeof closeMailPanel === 'function') closeMailPanel();
    if (window.mailMessages && window.mailMessages[id]) {
      window.mailMessages[id].forEach(function(m) { m.unread = false; });
    }
    if (typeof updateMailDot === 'function') updateMailDot();
    var sideEl = document.querySelector('.sn-item[data-client="' + id + '"]');
    selectClient(id, sideEl);
    setTimeout(function() {
      var tabs = document.querySelectorAll('.tab-item');
      if (tabs[1]) switchTab('chat', tabs[1]);
    }, 100);
    return;
  }
  if (_origSelectClientFromMail) _origSelectClientFromMail(id);
};

// ══════════════════════════════════════════════════════════════
// 9. Init — wait 600 ms so pro_dashboard.html's 500 ms sidebar
//    rebuild runs first; otherwise it overwrites our profile data
// ══════════════════════════════════════════════════════════════
document.addEventListener('DOMContentLoaded', function () {
  setTimeout(async function() {
    try {
      console.log('[NW-PATCH] Initializing pro dashboard with API data...');
      if (!window.NW) {
        console.warn('[NW-PATCH] NW not found, waiting for api.js');
        setTimeout(initProDashboard, 500);
        return;
      }
      await initProDashboard();
      console.log('[NW-PATCH] Pro dashboard initialized successfully');
    } catch (err) {
      console.error('[NW-PATCH] Failed to initialize pro dashboard:', err);
    }
  }, 600);
});
