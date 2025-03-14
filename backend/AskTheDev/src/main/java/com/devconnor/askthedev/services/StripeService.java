package com.devconnor.askthedev.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StripeService {

    public StripeService() {
        Dotenv dotenv = Dotenv.configure().load();
        Stripe.apiKey = dotenv.get("STRIPE_API_KEY");
    }

    public String createCheckoutSession(String priceId) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl("https://lesson-link.co.uk")
                .setCancelUrl("https://lesson-link.co.uk")
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPrice(priceId)
                        .build())
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }
}
