import { Status } from "../../utils/interfaces";
import { sendPostRequest } from "../requests";

async function logout(): Promise<Status> {
    return await sendPostRequest("/auth/logout", {});
}

export {
    logout
}