package com.devconnor.askthedev.controllers;

import com.devconnor.askthedev.controllers.request.PaymentRequest;
import com.devconnor.askthedev.controllers.response.ResponseUrl;
import com.devconnor.askthedev.services.payments.StripeService;
import com.stripe.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
        Event event = stripeService.validateAndRetrieveEvent(stripeSig, payload);
        stripeService.handleEvent(event);
        return ResponseEntity.ok("Purchase successful");
    }

    @PostMapping("/create-checkout")
    public ResponseEntity<ResponseUrl> createPaymentIntent(
            @RequestBody PaymentRequest paymentRequest
    ) {
        String url = stripeService.createCheckoutSession(paymentRequest.getSubscriptionType(), paymentRequest.getUserId());
        return ResponseEntity.ok(new ResponseUrl(url));
    }

    @GetMapping("/manage-subscription")
    public ResponseEntity<ResponseUrl> manageSubscription(
            @RequestParam("userId") UUID userId
    ) {
        String url = stripeService.createBillingPortalSession(userId);
        return ResponseEntity.ok(new ResponseUrl(url));
    }
}
