async function saveAuthToken(authToken: string): Promise<void> {
    try {
        await chrome.storage.local.set({
            atdAuth: authToken
        });
    } catch (err) {}
}

export {
    saveAuthToken
}