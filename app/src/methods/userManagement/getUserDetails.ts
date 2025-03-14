import { Status } from "../../utils/interfaces";
import { sendGetRequest } from "../requests";

async function getUserDetails(userId: string): Promise<Status> {
    return await sendGetRequest(`/user/${userId}`, []);
}

export {
    getUserDetails
}