const $ = id => document.getElementById(id);
const defaults = { url: 'http://localhost:8080/darija-translator/api/translate', username: 'translator', password: '' };
async function initialize() {
  const data = await chrome.storage.local.get([...Object.keys(defaults), 'selectedText', 'selectionTime']);
  for (const key of Object.keys(defaults)) $(key).value = data[key] ?? defaults[key];
  if (data.selectedText) $('text').value = data.selectedText;
  if (data.selectedText && data.selectionTime && Date.now() - data.selectionTime < 15000) await translate();
}
async function translate() {
  const text = $('text').value.trim();
  if (!text) { $('status').textContent = 'Please enter text.'; return; }
  $('translate').disabled = true; $('status').textContent = 'Translating…';
  try {
    const token = btoa(unescape(encodeURIComponent(`${$('username').value}:${$('password').value}`)));
    const response = await fetch($('url').value, { method: 'POST', headers: {'Content-Type': 'application/json', 'Authorization': `Basic ${token}`}, body: JSON.stringify({ text, sourceLanguage: $('language').value }) });
    const data = await response.json();
    if (!response.ok) throw new Error(data.error || `HTTP ${response.status}`);
    $('result').value = data.translation;
    $('status').textContent = 'Translation complete.';
  } catch (error) { $('status').textContent = `Error: ${error.message}`; }
  finally { $('translate').disabled = false; }
}
$('translate').addEventListener('click', translate);
$('save').addEventListener('click', async () => {
  await chrome.storage.local.set({url:$('url').value, username:$('username').value, password:$('password').value});
  $('status').textContent = 'Settings saved locally.';
});
chrome.storage.onChanged.addListener((changes, area) => {
  if (area === 'local' && changes.selectedText?.newValue) {
    $('text').value = changes.selectedText.newValue;
    translate();
  }
});
initialize();
