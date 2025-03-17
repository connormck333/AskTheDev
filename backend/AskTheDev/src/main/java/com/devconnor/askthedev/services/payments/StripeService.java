package com.devconnor.askthedev.services.payments;

import com.devconnor.askthedev.exception.ATDException;
import com.devconnor.askthedev.exception.CustomerNotFoundException;
import com.devconnor.askthedev.models.User;
import com.devconnor.askthedev.payments.EventManager;
import com.devconnor.askthedev.repositories.SubscriptionRepository;
import com.devconnor.askthedev.repositories.UserRepository;
import com.devconnor.askthedev.services.user.UserService;
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

import static com.devconnor.askthedev.utils.StripeEvents.*;

@Service
public class StripeService {

    private final UserService userService;
    private final EventManager eventManager;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public StripeService(
            UserService userService,
            EventManager eventManager,
            SubscriptionRepository subscriptionRepository,
            UserRepository userRepository
    ) {
        this.userService = userService;
        this.eventManager = eventManager;
        this.subscriptionRepository = subscriptionRepository;

        Dotenv dotenv = Dotenv.configure().load();
        Stripe.apiKey = dotenv.get("STRIPE_API_KEY");
        this.userRepository = userRepository;
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

        Session session = Session.create(params);
        return session.getUrl();
    }

    private Customer getCustomer(UUID userId) {
        User user = userService.getUserById(userId);
        if (user.getCustomerId() == null) {
            return createCustomer(user);
        }

        try {
            return Customer.retrieve(user.getCustomerId());
        } catch (StripeException e) {
            throw new CustomerNotFoundException();
        }
    }

    private Customer createCustomer(User user) {
        if (user == null || user.getCustomerId() != null) {
            throw new ATDException();
        }

        Map<String, String> metadata = new HashMap<>();
        metadata.put("user_id", String.valueOf(user.getId()));

        try {
            CustomerCreateParams params = CustomerCreateParams.builder()
                    .setEmail(user.getEmail())
                    .setMetadata(metadata)
                    .build();

            Customer customer = Customer.create(params);
            String customerId = customer.getId();

            user.setCustomerId(customerId);
            userRepository.save(user);

            return customer;
        } catch (StripeException e) {
            throw new ATDException();
        }
    }

    public void handleEvent(Event event) {
        switch (event.getType()) {
            case SUBSCRIPTION_CREATED -> eventManager.handleSubscriptionCreation(event);
            case SUBSCRIPTION_UPDATED -> eventManager.handleSubscriptionUpdated(event);
            case SUBSCRIPTION_DELETED -> eventManager.handleSubscriptionDeleted(event);
            case PAYMENT_SUCCESS -> eventManager.handleSuccessfulPayment(event);
            case PAYMENT_FAILED -> eventManager.handleFailedPayment(event);
        }
    }
}
