import { Status } from "../../utils/interfaces";
import { sendGetRequest } from "../requests";

async function createManageSubscriptionSession(userId: string): Promise<Status> {
    return await sendGetRequest("/payment/manage-subscription", [{
        key: "userId",
        value: userId
    }]);
}

export {
    createManageSubscriptionSession
}