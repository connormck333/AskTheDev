package com.devconnor.askthedev.utils;

import com.devconnor.askthedev.exception.SubscriptionNotFoundException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum SubscriptionType {
    FREE,
    BASIC(
            "price_1R2gpoIW6fDMtSqJFfgdq3ls", "price_1RAIpLIW6fDMtSqJIKlz9O7n",
            "prod_RwZzeVUoP7d3ke", "prod_S4RieR12SqHErT"
    ),
    PRO(
            "price_1R5U5xIW6fDMtSqJF6Zpxoml", "price_1RAIqBIW6fDMtSqJcaTsl2Ks",
            "prod_RzT2e6PEV9vQgs", "prod_S4RjAHqg5BZQqS"
    );

    private final String priceId;
    private final String productId;

    SubscriptionType() {
        this.priceId = "";
        this.productId = "";
    }

    SubscriptionType(String devPriceId, String prodPriceId, String devProductId, String prodProductId) {
        EnvironmentType envType = EnvUtils.getEnvType();
        this.priceId = envType == EnvironmentType.LOCAL ? devPriceId : prodPriceId;
        this.productId = envType == EnvironmentType.LOCAL ? devProductId : prodProductId;
    }

    public static SubscriptionType fromString(String value) {
        for (SubscriptionType subscriptionType : getSubscriptionTypes()) {
            if (subscriptionType.priceId.equalsIgnoreCase(value)) {
                return subscriptionType;
            }
        }

        throw new SubscriptionNotFoundException();
    }

    public static List<SubscriptionType> getSubscriptionTypes() {
        List<SubscriptionType> subscriptionTypes = new ArrayList<>();
        subscriptionTypes.add(BASIC);
        subscriptionTypes.add(PRO);

        return subscriptionTypes;
    }

    public int getPromptAmount() {
        return switch (this) {
            case FREE -> 3;
            case BASIC -> 15;
            case PRO -> 50;
        };
    }
}
