package com.devconnor.askthedev.utils;

import com.devconnor.askthedev.exception.SubscriptionNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriptionType {
    BASIC("prod_RwZzeVUoP7d3ke"),
    PRO("");

    private final String value;

    public static SubscriptionType fromString(String value) {
        for (SubscriptionType subscriptionType : SubscriptionType.values()) {
            if (subscriptionType.value.equalsIgnoreCase(value)) {
                return subscriptionType;
            }
        }

        throw new SubscriptionNotFoundException();
    }
}
