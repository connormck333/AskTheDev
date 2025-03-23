import { Status } from "../../utils/interfaces";
import { sendPostRequest } from "../requests";

interface CheckoutBody {
    userId: string,
    subscriptionType: string
}

async function createCheckoutSession(userId: string, tierId: string): Promise<Status> {
    const body: CheckoutBody = {
        userId: userId,
        subscriptionType: tierId
    }

    return await sendPostRequest("/payment/create-checkout", body);
}

export {
    createCheckoutSession
}