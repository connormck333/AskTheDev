import { Status } from "../../utils/interfaces";
import { sendPostRequest } from "../requests";

interface CheckoutBody {
    userId: string,
    amount: number
}

async function createCheckoutSession(userId: string, amount: number): Promise<Status> {
    const body: CheckoutBody = { userId, amount };

    return await sendPostRequest("/payment/create-checkout", body);
}

export {
    createCheckoutSession
}