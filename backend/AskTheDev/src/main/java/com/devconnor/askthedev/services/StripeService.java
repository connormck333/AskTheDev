package com.devconnor.askthedev.services;

import com.devconnor.askthedev.exception.ATDException;
import com.devconnor.askthedev.exception.CustomerNotFoundException;
import com.devconnor.askthedev.exception.InvalidUserIdException;
import com.devconnor.askthedev.models.ATDSubscription;
import com.devconnor.askthedev.models.User;
import com.devconnor.askthedev.repositories.SubscriptionRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.devconnor.askthedev.utils.StripeEvents.PAYMENT_SUCCESS;

@Service
public class StripeService {

    private final UserService userService;
    private final SubscriptionRepository subscriptionRepository;

    public StripeService(
            UserService userService,
            SubscriptionRepository subscriptionRepository
    ) {
        this.userService = userService;
        this.subscriptionRepository = subscriptionRepository;
        Dotenv dotenv = Dotenv.configure().load();
        Stripe.apiKey = dotenv.get("STRIPE_API_KEY");
    }

    public String createCheckoutSession(String priceId, UUID userId) throws StripeException {
        Customer customer = getCustomer(userId);
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl("https://lesson-link.co.uk")
                .setCancelUrl("https://lesson-link.co.uk")
                .setCustomer(customer.getId())
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPrice(priceId)
                        .build())
                .putMetadata("user_id", userId.toString())
                .build();

        System.out.println(customer.getId());

        Session session = Session.create(params);
        return session.getUrl();
    }

    private Customer getCustomer(UUID userId) {
        ATDSubscription subscription = subscriptionRepository.getSubscriptionByUserId(userId);
        if (subscription == null) {
            return createCustomer(userId);
        }

        try {
            return Customer.retrieve(subscription.getCustomerId());
        } catch (StripeException e) {
            throw new CustomerNotFoundException();
        }
    }

    private Customer createCustomer(UUID userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new InvalidUserIdException(userId);
        }

        Map<String, String> metadata = new HashMap<>();
        metadata.put("user_id", userId.toString());

        try {
            CustomerCreateParams params = CustomerCreateParams.builder()
                    .setEmail(user.getEmail())
                    .setMetadata(metadata)
                    .build();

            return Customer.create(params);
        } catch (StripeException e) {
            throw new ATDException();
        }
    }

    public void handleEvent(Event event) {
        switch (event.getType()) {
            case PAYMENT_SUCCESS -> handleSuccessfulPayment(event);
        }
    }

    private void handleSuccessfulPayment(Event event) {
        System.out.println("Payment successful");
    }
}
