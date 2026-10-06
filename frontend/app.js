'use strict';
(() => {
  const $ = id => document.getElementById(id);
  const escape = value => String(value ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
  const state = { user: null, questions: [], groups: [], selected: new Set(), requests: [], modal: null, busy: false, questionLoaded: false, groupLoaded: false, generation: 0 };
  const labels = { LEETCODE:'LeetCode', CODEFORCES:'Codeforces', CODECHEF:'CodeChef', OTHER:'Other' };
  let saved = {};
  try { saved = JSON.parse(localStorage.getItem('wmc-ui-connection') || '{}'); } catch (_) {}
  $('base-url').value = saved.base || (location.protocol === 'file:' ? 'http://localhost:8080' : location.origin);
  $('user-id').value = saved.userId || '';
  $('dsa-path-id').value = saved.pathId || '1';
  const config = () => ({ base: $('base-url').value.trim().replace(/\/+$/, ''), userId: $('user-id').value.trim(), pathId: $('dsa-path-id').value.trim() || '1' });
  const routes = () => {
    const { userId, pathId } = config();
    const u = encodeURIComponent(userId || '{userId}'), d = encodeURIComponent(pathId);
    return { profile:`/wmc/api/v1/users/${u}`, questions:`/wmc/api/v1/users/${u}/sheets/dsa/${d}`, add:`/wmc/api/v1/users/${u}/sheet/dsa/add`, delete:`/wmc/api/v1/users/${u}/sheet/dsa/delete`, groups:`/wmd/api/v1/user/${u}/group`, create:`/wmd/api/v1/user/${u}/group/create`, join:`/wmd/api/v1/user/${u}/group/join`, check:`/wmd/api/v1/user/${u}/group/checkName` };
  };
  function updateBadges() { $('questions-api').textContent = routes().questions; $('groups-api').textContent = routes().groups; }
  function notice(id, message, type = '') { const el = $(id); el.textContent = message; el.className = `notice ${type}`; el.hidden = !message; }
  function toast(message) { $('toast').textContent = message; $('toast').hidden = false; clearTimeout(toast.timer); toast.timer = setTimeout(() => { $('toast').hidden = true; }, 4500); }
  function requireConnection() {
    const { base, userId } = config();
    if (!base || !userId) { $('connection-panel').open = true; $('user-id').focus(); throw new Error('Set the API base URL and an existing user ID first.'); }
    let url; try { url = new URL(base); } catch (_) { throw new Error('Enter a valid API base URL.'); }
    if (!['http:', 'https:'].includes(url.protocol)) throw new Error('The API base URL must start with http:// or https://.');
    return base;
  }
  const redact = body => body == null ? null : JSON.parse(JSON.stringify(body, (key, value) => /password/i.test(key) ? '[masked]' : value));
  const format = body => body == null || body === '' ? '(empty body)' : typeof body === 'string' ? body : JSON.stringify(body, null, 2);
  async function request(label, method, path, body, acceptAvailable = false) {
    const base = requireConnection();
    const entry = { id: Date.now() + Math.random(), label, method, url:base + path, path, requestBody:redact(body), startedAt:new Date().toISOString(), status:null, responseBody:null, duration:null, outcome:'pending', note:'' };
    state.requests.unshift(entry); renderActivity();
    const start = performance.now(), controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 30000);
    try {
      const response = await fetch(entry.url, { method, headers: body === undefined ? { Accept:'application/json' } : { Accept:'application/json', 'Content-Type':'application/json' }, ...(body === undefined ? {} : { body:JSON.stringify(body) }), signal:controller.signal });
      entry.status = response.status;
      const raw = await response.text(); let data = raw || null;
      if (raw) { try { data = JSON.parse(raw); } catch (_) {} }
      entry.responseBody = data;
      const available = acceptAvailable && response.status === 404 && !raw;
      entry.outcome = response.ok || available ? 'success' : 'error';
      if (!response.ok && !available) {
        const message = data && typeof data === 'object' && data.message ? data.message : typeof data === 'string' && data.trim() ? data.slice(0, 250) : response.statusText || 'Request failed';
        throw new Error(`HTTP ${response.status}${data?.code ? ` · ${data.code}` : ''}: ${message}`);
      }
      return { data, status:response.status, entry, available };
    } catch (error) {
      entry.outcome = 'error';
      if (entry.status === null) {
        const message = error.name === 'AbortError' ? 'Request timed out after 30 seconds. The backend result is unknown; inspect data before retrying.' : 'Could not reach the API. Check the backend address and whether it is running. If using a separate UI server, the backend must allow that origin.';
        entry.responseBody = { clientError:message }; throw new Error(message);
      }
      throw error;
    } finally { clearTimeout(timeout); entry.duration = Math.round(performance.now() - start); renderActivity(); }
  }
  function date(value) { if (!value) return '—'; const parsed = new Date(value); return Number.isNaN(parsed.getTime()) ? String(value) : parsed.toLocaleDateString(undefined, { day:'numeric', month:'short', year:'numeric' }); }
  function initials(name) { return String(name || '?').split(/[\s_-]+/).filter(Boolean).slice(0, 2).map(p => p[0]).join('').toUpperCase(); }
  function safeLink(value) { try { const link = new URL(value); return ['http:', 'https:'].includes(link.protocol) ? link.href : null; } catch (_) { return null; } }
  function renderQuestions() {
    $('question-count').textContent = state.questionLoaded ? `${state.questions.length} question${state.questions.length === 1 ? '' : 's'}` : 'Not loaded';
    $('question-empty').hidden = state.questions.length > 0;
    if (state.questionLoaded && !state.questions.length) $('question-empty').innerHTML = '<div class="empty-icon" aria-hidden="true">&lt;/&gt;</div><h2>No questions yet</h2><p>Add your first question to your personal tracker.</p>';
    $('question-rows').innerHTML = state.questions.map((row, index) => {
      const ids = Array.isArray(row.groupIds) ? row.groupIds : [], link = safeLink(row.url);
      return `<tr class="${state.selected.has(String(row.id)) ? 'selected' : ''}"><td class="select-cell"><input type="checkbox" data-select="${escape(row.id)}" aria-label="Select ${escape(row.title || `question ${row.id}`)}" ${state.selected.has(String(row.id)) ? 'checked' : ''}></td><td>${index + 1}</td><td class="title-cell">${escape(row.title)}</td><td>${link ? `<a class="url-link" href="${escape(link)}" title="${escape(row.url)}" target="_blank" rel="noopener noreferrer">${escape(row.url)}</a>` : `<span class="url-link" title="${escape(row.url)}">${escape(row.url)}</span>`}</td><td><span class="source-tag ${escape(String(row.source || '').toLowerCase())}">${escape(labels[row.source] || row.source || '—')}</span></td><td>${escape(date(row.submittedAt))}</td><td><div class="group-tags">${ids.length ? ids.map(id => `<span class="group-tag" title="${escape(id)}">${escape(state.groups.find(g => g.groupId === id)?.displayName || state.groups.find(g => g.groupId === id)?.groupName || id)}</span>`).join('') : '<span class="subtle">Personal</span>'}</div></td><td class="notes-cell">${escape(row.notes || '—')}</td><td><button class="pending-button" type="button" data-pending="update" aria-disabled="true" title="The backend has no active PATCH endpoint yet.">Update</button><span class="pending-caption">API not exposed</span></td></tr>`;
    }).join('');
    updateSelection();
  }
  function updateSelection() {
    $('delete-questions').disabled = !state.selected.size;
    $('selection-count').textContent = state.selected.size ? `(${state.selected.size})` : '';
    $('select-all').disabled = !state.questions.length;
    $('select-all').checked = state.questions.length > 0 && state.selected.size === state.questions.length;
    $('select-all').indeterminate = state.selected.size > 0 && state.selected.size < state.questions.length;
  }
  function renderGroups() {
    $('group-count').textContent = state.groupLoaded ? `${state.groups.length} group${state.groups.length === 1 ? '' : 's'} joined` : 'Your practice circles';
    $('group-list').innerHTML = state.groups.length ? state.groups.map((g, index) => `<button class="group-item" data-group-index="${index}"><span class="group-avatar">${escape(initials(g.displayName || g.groupName))}</span><span class="group-copy"><span class="group-name">${escape(g.displayName || g.groupName)}</span><span class="group-meta">Members: ${escape(g.membersCount ?? '—')} ${g.adminId === config().userId ? ' · Admin' : ''}</span></span><span class="group-arrow">›</span></button>`).join('') : `<div class="group-empty"><span aria-hidden="true">◎</span><h3>${state.groupLoaded ? 'No groups joined yet' : 'No groups loaded'}</h3><p>${state.groupLoaded ? 'Create a group or join one with its code.' : 'Load your home page to see your groups.'}</p></div>`;
  }
  async function loadProfile(generation = state.generation) {
    const r = await request('Read user profile', 'GET', routes().profile);
    if (generation !== state.generation) return;
    if (!r.data || typeof r.data !== 'object' || Array.isArray(r.data)) throw new Error('The profile API did not return a user object. Inspect the response in API activity.');
    state.user = r.data; $('profile-button').textContent = initials(r.data.displayName || r.data.userName); $('profile-button').title = r.data.displayName || r.data.userName || 'User profile';
    $('connection-dot').classList.add('connected'); $('connection-summary').textContent = `${r.data.displayName || r.data.userName || 'User loaded'} · ${config().base}`;
  }
  async function loadQuestions(generation = state.generation) {
    notice('questions-notice', 'Loading questions…');
    try {
      const r = await request('Read personal questions', 'GET', routes().questions);
      if (generation !== state.generation) return;
      if (!Array.isArray(r.data)) throw new Error('The questions API did not return an array. Inspect the response in API activity.');
      state.questions = r.data; state.questionLoaded = true;
      state.selected = new Set([...state.selected].filter(id => r.data.some(row => String(row.id) === id)));
      notice('questions-notice', ''); renderQuestions();
    } catch (error) { if (generation === state.generation) { notice('questions-notice', error.message, 'error'); if (!state.questionLoaded) $('question-empty').innerHTML = '<div class="empty-icon" aria-hidden="true">&lt;/&gt;</div><h2>Questions could not be loaded</h2><p>The API response is shown above.<br>View API activity for the request details.</p>'; } }
  }
  async function loadGroups(generation = state.generation) {
    notice('groups-notice', 'Loading groups…');
    try {
      const r = await request('Read joined groups', 'GET', routes().groups);
      if (generation !== state.generation) return;
      if (!Array.isArray(r.data)) throw new Error('The groups API did not return an array. Inspect the response in API activity.');
      state.groups = r.data; state.groupLoaded = true; notice('groups-notice', ''); renderGroups(); renderQuestions();
    } catch (error) { if (generation === state.generation) notice('groups-notice', error.message, 'error'); }
  }
  function inputField(id, label, placeholder = '', type = 'text', value = '') { return `<label>${escape(label)}<input id="${id}" type="${type}" value="${escape(value)}" placeholder="${escape(placeholder)}" autocomplete="off"></label>`; }
  function payload() {
    if (state.modal === 'add') return { title:$('question-title').value, url:$('question-url').value, source:$('question-source').value, groupIds:[...document.querySelectorAll('[name="share-group"]:checked')].map(el => el.value).concat(($('extra-group-ids').value || '').split(/[\s,]+/).filter(Boolean)), notes:$('question-notes').value };
    if (state.modal === 'create') return { groupName:$('group-name').value, displayName:$('group-display-name').value, password:$('group-password').value };
    if (state.modal === 'join') return { groupId:$('join-group-id').value, groupCode:$('join-code').value, password:$('join-password').value };
    if (state.modal === 'delete') return [...state.selected].map(Number);
    return null;
  }
  function updatePreview() { if (state.modal && !['profile','group'].includes(state.modal)) $('payload-preview').textContent = format(redact(payload())); }
  function openAction(kind, group) {
    if (state.busy) return;
    if (!['profile','group'].includes(kind)) { try { requireConnection(); } catch (error) { toast(error.message); return; } }
    state.modal = kind; notice('dialog-feedback', ''); $('action-form').reset(); $('submit-action').hidden = false; $('submit-action').disabled = false;
    const specs = { add:['Add Question','Save a question to your personal tracker.','POST',routes().add,'Save Question','primary'], create:['Create Group','Set a name and password for your new group.','POST',routes().create,'Create Group','green'], join:['Join Group','Enter the group code and password to join.','POST',routes().join,'Join Group','amber'], delete:['Delete Questions',`Delete ${state.selected.size} selected question${state.selected.size === 1 ? '' : 's'} from your tracker.`,'DELETE',routes().delete,'Delete Questions','danger'], profile:['User Profile','Your profile returned by the backend.','GET',routes().profile,'','primary'], group:[group?.displayName || group?.groupName || 'Group','Group details from the home-page response.','GET',routes().groups,'','primary'] };
    const [title, description, method, path, buttonLabel, color] = specs[kind];
    $('dialog-title').textContent = title; $('dialog-description').textContent = description; $('dialog-method').textContent = method; $('dialog-method').className = `method ${method.toLowerCase()}`; $('dialog-api').textContent = path; $('submit-action').textContent = buttonLabel; $('submit-action').className = `button ${color}`;
    let fields = '';
    if (kind === 'add') fields = `<div class="form-fields">${inputField('question-title','Title','e.g. Two Sum')}${inputField('question-url','URL','https://leetcode.com/problems/two-sum/')}<div class="two-fields"><label>Source<select id="question-source">${Object.entries(labels).map(([value,label]) => `<option value="${value}">${label}</option>`).join('')}</select></label><label>Submitted At<input value="Set by the backend" readonly><span class="hint">Recorded when the entry is saved.</span></label></div><label>Groups <span class="hint">Optional · select groups to share with</span></label><div class="group-options">${state.groups.length ? state.groups.map(g => `<label class="group-option"><input name="share-group" type="checkbox" value="${escape(g.groupId)}">${escape(g.displayName || g.groupName)}</label>`).join('') : '<span class="hint">No groups loaded. Leave empty for a personal question.</span>'}</div><details><summary class="small">Enter additional group IDs for testing</summary>${inputField('extra-group-ids','Group IDs','Comma-separated UUIDs')}</details><label>Notes<textarea id="question-notes" placeholder="Approach, complexity, edge cases…"></textarea></label></div>`;
    if (kind === 'create') fields = `<div class="form-fields"><label><span class="field-inline">Group Name<button type="button" class="text-button" id="check-name">Check availability</button></span><input id="group-name" placeholder="e.g. dsa-squad" autocomplete="off"><span class="field-result" id="name-result">GET ${escape(routes().check)}?groupName=…</span></label>${inputField('group-display-name','Display Name','e.g. DSA Squad')}${inputField('group-password','Password','Group password','password')}<span class="hint">The backend generates a group code. The current response does not return it.</span></div>`;
    if (kind === 'join') fields = `<div class="form-fields">${inputField('join-group-id','Group ID','Group UUID')}<span class="hint">The current join API requires the group ID as well as the code.</span>${inputField('join-code','Group Code','e.g. 23456789A')}${inputField('join-password','Password','Group password','password')}<span class="hint">Ask a group admin for the ID and code.</span></div>`;
    if (kind === 'delete') fields = `<div class="notice warning">This sends the selected IDs to your backend. Check the selection before deleting.</div><ul>${state.questions.filter(row => state.selected.has(String(row.id))).map(row => `<li>${escape(row.title)} <span class="small">#${escape(row.id)}</span></li>`).join('')}</ul>`;
    if (kind === 'profile' || kind === 'group') {
      $('submit-action').hidden = true;
      const data = kind === 'profile' ? state.user : group;
      fields = data ? `<dl class="dialog-data">${Object.entries(data).map(([key,value]) => `<dt>${escape(key)}</dt><dd>${escape(typeof value === 'object' ? JSON.stringify(value) : value ?? '—')}</dd>`).join('')}</dl>` : '<p class="read-only-note">Load your user profile first.</p>';
      if (kind === 'group') fields += '<p class="read-only-note">The group question-bank API is not exposed yet.</p>';
    }
    $('dialog-fields').innerHTML = fields; $('action-form').querySelector('.payload-preview').hidden = ['profile','group'].includes(kind); updatePreview(); $('action-dialog').showModal();
  }
  function closeAction() { if (state.busy) return; $('action-dialog').close(); state.modal = null; }
  function renderActivity() {
    $('request-count').textContent = state.requests.length; $('activity-summary').textContent = `${state.requests.length} request${state.requests.length === 1 ? '' : 's'} · passwords masked`;
    const expanded = new Set([...document.querySelectorAll('.request-item[open]')].map(el => el.dataset.request));
    $('activity-list').innerHTML = state.requests.length ? state.requests.map(r => `<details class="request-item" data-request="${r.id}" ${expanded.has(String(r.id)) ? 'open' : ''}><summary><span class="request-topline"><span class="method ${r.method.toLowerCase()}">${r.method}</span><span class="request-label">${escape(r.label)}</span><span class="http-status ${r.outcome === 'error' ? 'fail' : r.outcome === 'pending' ? 'pending' : ''}">${r.status ?? (r.outcome === 'pending' ? 'Sending…' : 'Network error')}</span></span><code class="request-path">${escape(r.url)}</code><span class="request-meta">${escape(new Date(r.startedAt).toLocaleTimeString())} ${r.duration !== null ? ` · ${r.duration} ms` : ''}</span></summary><div class="request-detail"><h3>Request body · password values masked</h3><pre>${escape(format(r.requestBody))}</pre><h3>Response body</h3><pre>${escape(format(r.responseBody))}</pre><label class="request-note">Your test observation<textarea data-note="${r.id}" placeholder="What did you expect? What happened?">${escape(r.note)}</textarea></label></div></details>`).join('') : '<div class="activity-empty">Use the home page to start testing.<br>Your requests and responses will appear here.</div>';
  }
  $('connection-form').addEventListener('submit', async event => {
    event.preventDefault(); try { requireConnection(); } catch(error) { toast(error.message); return; }
    const current = config(); try { localStorage.setItem('wmc-ui-connection', JSON.stringify(current)); } catch (_) {}
    const generation = ++state.generation; state.user = null; state.questions = []; state.groups = []; state.selected.clear(); state.questionLoaded = false; state.groupLoaded = false; $('profile-button').textContent = '?'; $('connection-dot').classList.remove('connected');
    $('question-empty').innerHTML = '<div class="empty-icon" aria-hidden="true">&lt;/&gt;</div><h2>Loading your questions</h2><p>Waiting for your backend response.</p>'; renderQuestions(); renderGroups(); updateBadges(); $('load-button').disabled = true; $('load-button').textContent = 'Loading…';
    const results = await Promise.allSettled([loadProfile(generation), loadQuestions(generation), loadGroups(generation)]);
    if (results[0].status === 'rejected') { $('connection-summary').textContent = results[0].reason.message; toast(results[0].reason.message); }
    $('load-button').disabled = false; $('load-button').textContent = 'Load home page';
    if (results[0].status === 'fulfilled') $('connection-panel').open = false;
  });
  ['base-url','user-id','dsa-path-id'].forEach(id => $(id).addEventListener('input', () => {
    ++state.generation; state.user = null; state.questions = []; state.groups = []; state.selected.clear(); state.questionLoaded = false; state.groupLoaded = false;
    $('profile-button').textContent = '?'; $('question-empty').innerHTML = '<div class="empty-icon" aria-hidden="true">&lt;/&gt;</div><h2>Your questions will appear here</h2><p>Load the home page with this connection.</p>';
    notice('questions-notice',''); notice('groups-notice',''); renderQuestions(); renderGroups(); updateBadges(); $('connection-dot').classList.remove('connected'); $('connection-summary').textContent = 'Connection changed · load the home page';
  }));
  $('refresh-questions').addEventListener('click', () => loadQuestions()); $('refresh-groups').addEventListener('click', () => loadGroups());
  $('add-question').addEventListener('click', () => openAction('add')); $('create-group').addEventListener('click', () => openAction('create')); $('join-group').addEventListener('click', () => openAction('join')); $('delete-questions').addEventListener('click', () => { if (state.selected.size) openAction('delete'); }); $('profile-button').addEventListener('click', () => openAction('profile'));
  $('question-rows').addEventListener('change', event => { const id = event.target.dataset.select; if (!id) return; event.target.checked ? state.selected.add(id) : state.selected.delete(id); event.target.closest('tr').classList.toggle('selected',event.target.checked); updateSelection(); });
  $('question-rows').addEventListener('click', event => { if (event.target.dataset.pending) toast('Update is shown in Miro, but there is no active API for it yet.'); });
  $('select-all').addEventListener('change', event => { state.selected = event.target.checked ? new Set(state.questions.map(row => String(row.id))) : new Set(); renderQuestions(); });
  $('group-list').addEventListener('click', event => { const el = event.target.closest('[data-group-index]'); if (el) openAction('group', state.groups[Number(el.dataset.groupIndex)]); });
  $('close-dialog').addEventListener('click', closeAction); $('cancel-dialog').addEventListener('click', closeAction); $('action-dialog').addEventListener('cancel', event => { if (state.busy) event.preventDefault(); else state.modal = null; });
  $('action-form').addEventListener('input', updatePreview); $('action-form').addEventListener('change', updatePreview);
  $('dialog-fields').addEventListener('input', event => { if (event.target.id === 'group-name') { $('name-result').textContent = `GET ${routes().check}?groupName=…`; $('name-result').style.color = ''; } });
  $('dialog-fields').addEventListener('click', async event => {
    if (event.target.id !== 'check-name') return;
    const button = event.target, name = $('group-name').value, result = $('name-result'); button.disabled = true;
    $('name-result').textContent = 'Checking…';
    try { const r = await request('Check group name','GET',`${routes().check}?groupName=${encodeURIComponent(name)}`,undefined,true); if (result.isConnected && $('group-name').value === name) { result.textContent = r.available ? 'Available · HTTP 404 (empty response)' : `HTTP ${r.status} · inspect API activity`; result.style.color = '#059669'; } }
    catch(error) { if (result.isConnected && $('group-name').value === name) { result.textContent = error.message; result.style.color = '#dc2626'; } }
    finally { button.disabled = false; }
  });
  $('action-form').addEventListener('submit', async event => {
    event.preventDefault(); if (state.busy || !['add','create','join','delete'].includes(state.modal)) return;
    const kind = state.modal, button = $('submit-action'), label = button.textContent, generation = state.generation;
    state.busy = true; button.disabled = true; button.textContent = 'Sending…'; $('cancel-dialog').disabled = true; $('close-dialog').disabled = true; notice('dialog-feedback','Sending request…');
    try {
      const r = await request(label,kind === 'delete' ? 'DELETE' : 'POST',routes()[kind],payload());
      notice('dialog-feedback',`HTTP ${r.status} · ${kind === 'add' ? 'Question saved.' : kind === 'create' ? 'Group created.' : kind === 'join' ? 'Joined group.' : 'Delete request completed.'} See API activity for the full response.`, 'success');
      if (kind === 'add' || kind === 'delete') await loadQuestions(generation); else await loadGroups(generation);
    } catch(error) { notice('dialog-feedback',error.message,'error'); }
    finally { state.busy = false; button.disabled = false; button.textContent = label; $('cancel-dialog').disabled = false; $('close-dialog').disabled = false; }
  });
  ['inspect-button','inspect-button-bottom'].forEach(id => $(id).addEventListener('click', () => { renderActivity(); $('inspector').showModal(); }));
  $('close-inspector').addEventListener('click', () => $('inspector').close());
  $('activity-list').addEventListener('input', event => { if (event.target.dataset.note) { const item = state.requests.find(r => String(r.id) === event.target.dataset.note); if (item) item.note = event.target.value; } });
  $('export-requests').addEventListener('click', () => {
    const file = new Blob([JSON.stringify({ exportedAt:new Date().toISOString(), requests:state.requests },null,2)],{type:'application/json'}), url = URL.createObjectURL(file), link = document.createElement('a');
    link.href = url; link.download = `wemakecoder-api-activity-${new Date().toISOString().slice(0,10)}.json`; link.click(); setTimeout(() => URL.revokeObjectURL(url), 1000);
  });
  updateBadges();
})();
