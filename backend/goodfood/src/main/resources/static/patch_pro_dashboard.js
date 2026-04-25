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
        // Update name if API has a better one
        if (client.name) clients[bindKey].name = client.name;
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

    _updateOverviewStats(list);
  } catch (e) {
    console.warn('[NW] initProDashboard failed:', e.message);
  }
}

/** Update overview table with API client data */
function _updateOverviewStats(list) {
  var tbody = document.querySelector('#clientsTable tbody, .overview-table tbody');
  if (!tbody) return;
  tbody.innerHTML = list.map(function(c) {
    var s = c.stats || {};
    var statusClass = s.status === 'active' ? 'green' : s.status === 'warning' ? 'amber' : 'red';
    return '<tr onclick="selectClientFromTable(\'' + c.userId + '\')" style="cursor:pointer">'
      + '<td>' + escapeHtml(c.name) + '</td>'
      + '<td>' + escapeHtml(c.email) + '</td>'
      + '<td>' + (s.diaryCount || 0) + ' entries</td>'
      + '<td>' + (s.lastDiaryDate || '—') + '</td>'
      + '<td><span class="status-dot ' + statusClass + '"></span> ' + (s.status || 'unknown') + '</td>'
      + '</tr>';
  }).join('');
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

  // Execute original UI switch
  if (_origSelectClient) _origSelectClient(id, el);

  // Get API id — works for both static clients and bind_* clients
  var apiId = clients[id] ? clients[id]._apiId : null;
  if (!apiId) return;

  var today = new Date().toISOString().split('T')[0];
  var monthAgo = new Date(Date.now() - 30 * 24 * 3600 * 1000).toISOString().split('T')[0];

  var results = await Promise.allSettled([
    NW.messages.get(apiId),
    NW.appointments.getAll(),
    NW.clients.getDiary(apiId, monthAgo, today)
  ]);

  var c = clients[id];
  var messagesRes = results[0];
  var appointmentsRes = results[1];
  var diaryRes = results[2];

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
    if (window.mailMessages && window.mailMessages[id] !== undefined) {
      window.mailMessages[id] = c.messages
        .filter(function(m) { return m.from === 'client'; })
        .map(function(m) { return { from: id, text: m.text, time: m.time, unread: false }; });
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

  // ── Diary summary ──
  if (diaryRes.status === 'fulfilled') {
    var data = diaryRes.value || {};
    var meals = data.meals;
    var summary = data.summary;
    if (summary) {
      if (summary.avgKcal) c.stats.avg_kcal = summary.avgKcal;
      if (summary.avgProtein) c.stats.protein = summary.avgProtein;
      if (summary.exerciseDays) c.stats.exercise_days = summary.exerciseDays;
    }
    if (Array.isArray(meals) && meals.length > 0) {
      _buildCalorieTrend(c, meals);
    }
    if (typeof renderStats === 'function') renderStats(c);
  }
};

/** Build 30-day calorie trend array from API diary data */
function _buildCalorieTrend(c, meals) {
  var kcalByDate = {};
  meals.forEach(function(m) { kcalByDate[m.date] = (kcalByDate[m.date] || 0) + (m.kcal || 0); });
  var trend = [];
  for (var i = 29; i >= 0; i--) {
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
// 6. Logout
// ══════════════════════════════════════════════════════════════
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
// 7. Init
// ══════════════════════════════════════════════════════════════
document.addEventListener('DOMContentLoaded', function () {
  initProDashboard();
});
