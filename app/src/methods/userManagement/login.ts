import { Status } from "../../utils/interfaces";
import SubscriptionType from "../../utils/SubscriptionType";
import { sendPostRequest } from "../requests";
import { saveAuthToken } from "./saveAuthToken";

async function login(email: string, password: string): Promise<Status> {
    const response: Status = await sendPostRequest("/auth/login", {
        email: email,
        password: password
    });

    if (!response.success) return response;

    const data = response.data;
    if (data.subscriptionType === "BASIC") {
        response.data.subscriptionType = SubscriptionType.BASIC;
    } else if (data.subscriptionType === "PRO") {
        response.data.subscriptionType = SubscriptionType.PRO;
    }

    await saveAuthToken(response.data.authToken);

    return response;
}

export {
    login
}