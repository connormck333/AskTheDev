import { Status } from "../../utils/interfaces";

async function getCurrentTabHTML(): Promise<Status> {
    try {
        const tabs = await chrome.tabs.query({ active: true, currentWindow: true });
        const response = await chrome.scripting.executeScript({
            target: { tabId: tabs[0]?.id ? tabs[0].id : 0 },
            func: () => document.documentElement.outerHTML
        });

        if (response[0].result === undefined) {
            return { success: false };
        }

        return { success: true, data: removeAllTags(response[0].result) };
    } catch (error) {
        console.log(error);
        return { success: false }
    }
}

function removeAllTags(htmlContent: string): string {
    const cleanHtml: string = htmlContent.replace(/<script[^>]*>[\s\S]*?<\/script>|<style[^>]*>[\s\S]*?<\/style>/gi, "");
    const doc: Document = new DOMParser().parseFromString(cleanHtml, "text/html");
    const text = doc.body.textContent || "";

    return text.replace(/\s+/g, " ").trim();
}

export {
    getCurrentTabHTML
}