package com.devconnor.askthedev.controllers;

import com.devconnor.askthedev.controllers.request.PaymentRequest;
import com.devconnor.askthedev.controllers.response.CheckoutSession;
import com.devconnor.askthedev.services.StripeService;
import com.devconnor.askthedev.services.SubscriptionService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentsController {

    private final StripeService stripeService;
    private final SubscriptionService subscriptionService;
    private final String ENDPOINT_SECRET;

    public PaymentsController(
            StripeService stripeService,
            SubscriptionService subscriptionService
    ) {
        this.stripeService = stripeService;
        this.subscriptionService = subscriptionService;
        Dotenv dotenv = Dotenv.configure().load();
        this.ENDPOINT_SECRET = dotenv.get("STRIPE_ENDPOINT_SECRET");
    }

    @PostMapping("/purchase")
    public ResponseEntity<String> handlePurchase(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String stripeSig
    ) throws SignatureVerificationException {
        Event event = validateAndRetrieveEvent(stripeSig, payload);
        stripeService.handleEvent(event);
        return ResponseEntity.ok("Purchase successful");
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

    private Event validateAndRetrieveEvent(String stripeSignature, String eventPayload) throws SignatureVerificationException {
        return Webhook.constructEvent(eventPayload, stripeSignature, ENDPOINT_SECRET);
    }
}
