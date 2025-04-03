import { SendPromptBody, Status } from "../../utils/interfaces";
import { getCurrentTabHTML } from "../chrome/getCurrentTabHTML";
import { getCurrentWebUrl } from "../chrome/getCurrentWebUrl";
import { sendPostRequest } from "../requests";

async function summariseWebPage(userId: string, model: string): Promise<Status> {
    const tabTextContent = await getCurrentTabHTML();
    if (!tabTextContent.success) {
        return { success: false };
    }

    // const tabTextContent = {
    //     data: "<fake context, you can ignore this for now>"
    // }

    const webUrl: Status = await getCurrentWebUrl();
    if (!webUrl.success) {
        return { success: false };
    }

    const body: SendPromptBody = {
        userPrompt: "",
        pageContent: tabTextContent.data as string,
        webUrl: webUrl.data as string,
        modelType: model
    }

    return await sendPostRequest(`/prompt/summarise/${userId}`, body);
}

export {
    summariseWebPage
}