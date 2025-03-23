import { Status } from "../../utils/interfaces";
import SubscriptionType from "../../utils/SubscriptionType";
import { sendGetRequest } from "../requests";

async function getCurrentUser(): Promise<Status> {
    const response: Status = await sendGetRequest("/user/current-user", []);
    if (!response.success) return response;

    const data = response.data;
    if (data.subscriptionType === "BASIC") {
        response.data.subscriptionType = SubscriptionType.BASIC;
    } else if (data.subscriptionType === "PRO") {
        response.data.subscriptionType = SubscriptionType.PRO;
    }

    return response;
}

export {
    getCurrentUser
}