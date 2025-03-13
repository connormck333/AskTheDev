package com.devconnor.askthedev.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    public StripeService() {
        Dotenv dotenv = Dotenv.configure().load();
        Stripe.apiKey = dotenv.get("STRIPE_API_KEY");
    }

    public PaymentIntent createPaymentIntent(String token, double amount) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (amount * 100))
                .setCurrency("GBP")
                .setPaymentMethod(token)
                .setConfirm(true)
                .build();

        return PaymentIntent.create(params);
    }
}
