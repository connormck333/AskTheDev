import SubscriptionType from "./SubscriptionType";

function deriveSubscriptionType(subscriptionType: any): SubscriptionType {
    switch (subscriptionType) {
        case "FREE": return SubscriptionType.FREE;
        case "BASIC": return SubscriptionType.BASIC;
        case "PRO": return SubscriptionType.PRO;
        default: return SubscriptionType.NONE;
    }
}

export {
    deriveSubscriptionType
}