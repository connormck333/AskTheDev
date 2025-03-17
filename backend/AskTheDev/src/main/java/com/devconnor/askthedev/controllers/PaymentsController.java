package com.devconnor.askthedev.controllers;

import com.devconnor.askthedev.controllers.request.PaymentRequest;
import com.devconnor.askthedev.controllers.response.CheckoutSession;
import com.devconnor.askthedev.services.payments.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentsController {

    private final StripeService stripeService;

    @PostMapping("/event")
    public ResponseEntity<String> handleStripeEvent(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String stripeSig
    ) {
        try {
            Event event = stripeService.validateAndRetrieveEvent(stripeSig, payload);
            stripeService.handleEvent(event);
            return ResponseEntity.ok("Purchase successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Bad Request");
        }
    }

    @PostMapping("/create-checkout")
    public ResponseEntity<CheckoutSession> createPaymentIntent(
            @RequestBody PaymentRequest paymentRequest
    ) {
        try {
            String url = stripeService.createCheckoutSession("price_1R2gpoIW6fDMtSqJFfgdq3ls", paymentRequest.getUserId());
            return ResponseEntity.ok(new CheckoutSession(url));
        } catch (StripeException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
