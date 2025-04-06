import { Status } from "../../utils/interfaces";
import { sendPostRequest } from "../requests";
import { saveAuthToken } from "./saveAuthToken";

interface CreateAccountBody {
    email: string;
    password: string;
    termsAccepted: boolean;
}

async function createAccount(email: string, password: string): Promise<Status> {
    const body: CreateAccountBody = {
        email: email,
        password: password,
        termsAccepted: true
    }

    const response: Status = await sendPostRequest("/auth/signup", body);
    if (!response.success) return response;

    await saveAuthToken(response.data.authToken);

    return response;
}

export {
    createAccount
}