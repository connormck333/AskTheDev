package com.devconnor.askthedev.utils;

import com.devconnor.askthedev.exception.SubscriptionNotFoundException;
import lombok.Getter;

@Getter
public enum SubscriptionType {
    BASIC("price_1R2gpoIW6fDMtSqJFfgdq3ls", "price_1RAIpLIW6fDMtSqJIKlz9O7n"),
    PRO("price_1R5U5xIW6fDMtSqJF6Zpxoml", "price_1RAIqBIW6fDMtSqJcaTsl2Ks");

    private final String value;

    SubscriptionType(String devPriceId, String prodPriceId) {
        EnvironmentType envType = EnvUtils.getEnvType();
        this.value = envType == EnvironmentType.LOCAL ? devPriceId : prodPriceId;
    }

    public static SubscriptionType fromString(String value) {
        for (SubscriptionType subscriptionType : SubscriptionType.values()) {
            if (subscriptionType.value.equalsIgnoreCase(value)) {
                return subscriptionType;
            }
        }

        throw new SubscriptionNotFoundException();
    }

    public static String getProductId(SubscriptionType subscriptionType) {
        EnvironmentType envType = EnvUtils.getEnvType();
        return switch (subscriptionType) {
            case BASIC -> envType == EnvironmentType.LOCAL ? "prod_RwZzeVUoP7d3ke" : "prod_S4RieR12SqHErT";
            case PRO -> envType == EnvironmentType.LOCAL ? "prod_RzT2e6PEV9vQgs" : "prod_S4RjAHqg5BZQqS";
        };
    }

    public static int getPromptAmount(SubscriptionType subscriptionType) {
        return switch (subscriptionType) {
            case BASIC -> 15;
            case PRO -> 50;
        };
    }
}
