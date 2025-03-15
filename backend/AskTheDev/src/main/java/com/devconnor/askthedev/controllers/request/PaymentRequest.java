package com.devconnor.askthedev.controllers.request;

import com.devconnor.askthedev.utils.SubscriptionType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PaymentRequest {
    private UUID userId;
    private SubscriptionType subscriptionType;
}
