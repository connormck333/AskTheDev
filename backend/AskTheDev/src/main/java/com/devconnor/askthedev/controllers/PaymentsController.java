package com.devconnor.askthedev.controllers;

import com.devconnor.askthedev.controllers.request.PaymentRequest;
import com.devconnor.askthedev.controllers.response.CheckoutSession;
import com.devconnor.askthedev.services.StripeService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentsController {

    private final StripeService stripeService;

    @PostMapping("/purchase")
    public ResponseEntity<String> handlePurchase() {
        return ResponseEntity.ok("Purchase successful");
    }

    @PostMapping("/create-checkout")
    public ResponseEntity<CheckoutSession> createPaymentIntent(
            @RequestBody PaymentRequest paymentRequest
    ) {
        try {
            String url = stripeService.createCheckoutSession("price_1R2gpoIW6fDMtSqJFfgdq3ls");
            return ResponseEntity.ok(new CheckoutSession(url));
        } catch (StripeException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
