import { Status } from "../../utils/interfaces";
import { sendPostRequest } from "../requests";

async function logout(): Promise<Status> {
    const response: Status = await sendPostRequest("/auth/logout", {});
    if (response.success) {
        try {
            await chrome.storage.local.remove("atdAuth");
        } catch (err) {}
    }

    return response;
}

export {
    logout
}