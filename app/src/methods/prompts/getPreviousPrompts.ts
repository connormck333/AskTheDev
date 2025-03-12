import { GetParam, Status } from "../../utils/interfaces";
import { getCurrentWebUrl } from "../chrome/getCurrentWebUrl";
import { sendGetRequest } from "../requests";

async function getPreviousPromptsByPage(userId: number, minPage: number): Promise<Status> {
    const urlResponse: Status = await getCurrentWebUrl();
    if (!urlResponse.success) {
        return { success: false };
    }

    const webUrl: string = urlResponse.data;
    // const webUrl: string = "http://localhost:5173";

    const params: GetParam[] = [];
    params.push({key: "id", value: userId});
    params.push({key: "webUrl", value: webUrl});
    params.push({key: "minPage", value: minPage});

    return await sendGetRequest("/prompt/retrieve", params);
}

export {
    getPreviousPromptsByPage
}