import { Status } from "../../utils/interfaces";
import { sendGetRequest } from "../requests";

async function getCurrentUser(): Promise<Status> {
    return await sendGetRequest("/user/current-user", []);
}

export {
    getCurrentUser
}