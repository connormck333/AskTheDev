import { Status } from "../../utils/interfaces";
import { deriveSubscriptionType } from "../../utils/utils";
import { sendPostRequest } from "../requests";
import { saveAuthToken } from "./saveAuthToken";

async function login(email: string, password: string): Promise<Status> {
    const response: Status = await sendPostRequest("/auth/login", {
        email: email,
        password: password
    });
    if (!response.success) return response;

    response.data.subscriptionType = deriveSubscriptionType(response.data.subscriptionType);
    await saveAuthToken(response.data.authToken);

    return response;
}

export {
    login
}