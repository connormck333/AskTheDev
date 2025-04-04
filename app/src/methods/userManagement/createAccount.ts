import { Status } from "../../utils/interfaces";
import { sendPostRequest } from "../requests";

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
    return await sendPostRequest("/auth/signup", body);
}

export {
    createAccount
}