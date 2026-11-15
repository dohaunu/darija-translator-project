chrome.runtime.onInstalled.addListener(() => {
  chrome.contextMenus.create({ id: 'translate-darija', title: 'Translate selection to Darija', contexts: ['selection'] });
  chrome.sidePanel.setPanelBehavior({ openPanelOnActionClick: true });
});
chrome.contextMenus.onClicked.addListener(async (info, tab) => {
  if (info.menuItemId !== 'translate-darija' || !tab?.windowId) return;
  await chrome.storage.local.set({ selectedText: info.selectionText || '', selectionTime: Date.now() });
  await chrome.sidePanel.open({ windowId: tab.windowId });
});
