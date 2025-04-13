import { Status } from "../../utils/interfaces";
import { deriveSubscriptionType } from "../../utils/utils";
import { sendGetRequest } from "../requests";

async function getCurrentUser(): Promise<Status> {
    const response: Status = await sendGetRequest("/user/current-user", []);
    if (!response.success) return response;

    response.data.subscriptionType = deriveSubscriptionType(response.data.subscriptionType);

    return response;
}

export {
    getCurrentUser
}