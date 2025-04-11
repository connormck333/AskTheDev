import { Status } from "../../utils/interfaces";
import { deriveSubscriptionType } from "../../utils/utils";
import { sendPostRequest } from "../requests";

async function subscribeFreeAccount(userId: string): Promise<Status> {
    console.log(userId);
    const response: Status = await sendPostRequest("/payment/register-free-account", {userId: userId});
    if (!response.success) return response;

    response.data.subscriptionType = deriveSubscriptionType(response.data.subscriptionType);

    return response;
}

export {
    subscribeFreeAccount
}