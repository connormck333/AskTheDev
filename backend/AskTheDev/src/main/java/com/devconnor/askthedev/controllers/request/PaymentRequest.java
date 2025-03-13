package com.devconnor.askthedev.controllers.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private String token;
    private long amount;
}
