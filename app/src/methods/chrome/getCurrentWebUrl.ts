import { Status } from "../../utils/interfaces";

async function getCurrentWebUrl(): Promise<Status> {
    try {
        const tabs = await chrome.tabs.query({ active: true, currentWindow: true });
        
        if (tabs.length === 0) {
            return { success: false };
        }

        return { success: true, data: tabs[0].url };
    } catch (error) {
        return { success: false };
    }
}

export {
    getCurrentWebUrl
}