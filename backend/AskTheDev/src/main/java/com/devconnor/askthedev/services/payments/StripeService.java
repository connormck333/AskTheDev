package com.devconnor.askthedev.services.payments;

import com.devconnor.askthedev.exception.ATDException;
import com.devconnor.askthedev.exception.CustomerNotFoundException;
import com.devconnor.askthedev.exception.InvalidEventException;
import com.devconnor.askthedev.exception.UserNotFoundException;
import com.devconnor.askthedev.models.User;
import com.devconnor.askthedev.repositories.UserRepository;
import com.devconnor.askthedev.services.user.UserService;
import com.devconnor.askthedev.utils.SubscriptionType;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.billingportal.Configuration;
import com.stripe.param.billingportal.ConfigurationCreateParams.Features.SubscriptionUpdate.Product;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.billingportal.ConfigurationCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.devconnor.askthedev.utils.StripeEvents.*;

@Service
public class StripeService {

    private final UserService userService;
    private final EventManager eventManager;
    private final UserRepository userRepository;

    private final String stripeEndpointSecret;

    public StripeService(
            UserService userService,
            EventManager eventManager,
            UserRepository userRepository
    ) {
        this.userService = userService;
        this.eventManager = eventManager;

        Dotenv dotenv = Dotenv.configure().load();
        Stripe.apiKey = dotenv.get("STRIPE_API_KEY");
        this.stripeEndpointSecret = dotenv.get("STRIPE_ENDPOINT_SECRET");
        this.userRepository = userRepository;
    }

    public Event validateAndRetrieveEvent(String stripeSignature, String eventPayload) {
        try {
            return Webhook.constructEvent(eventPayload, stripeSignature, stripeEndpointSecret);
        } catch (SignatureVerificationException e) {
            throw new InvalidEventException();
        }
    }

    public String createCheckoutSession(SubscriptionType subscriptionType, UUID userId) {
        Customer customer = getCustomer(userId, true);
        String priceId = subscriptionType.getValue();

        try {
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
        } catch (StripeException e) {
            throw new ATDException();
        }
    }

    public String createBillingPortalSession(UUID userId) {
        Customer customer = getCustomer(userId, false);

        try {
            Configuration config = createBillingPortalConfig();
            com.stripe.param.billingportal.SessionCreateParams params = com.stripe.param.billingportal.SessionCreateParams.builder()
                    .setConfiguration(config.getId())
                    .setCustomer(customer.getId())
                    .setReturnUrl("https://lesson-link.co.uk")
                    .build();
            com.stripe.model.billingportal.Session session = com.stripe.model.billingportal.Session.create(params);

            return session.getUrl();
        } catch (StripeException e) {
            throw new ATDException("Failed to create billing portal session.");
        }
    }

    private Customer getCustomer(UUID userId, boolean createIfNotExists) {
        User user = userService.findById(userId);
        if (user == null) {
            throw new UserNotFoundException();
        } else if (user.getCustomerId() == null) {
            if (createIfNotExists) {
                return createCustomer(user);
            }
            throw new CustomerNotFoundException();
        }

        try {
            return Customer.retrieve(user.getCustomerId());
        } catch (StripeException e) {
            throw new CustomerNotFoundException();
        }
    }

    private Customer createCustomer(User user) {
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

    private Configuration createBillingPortalConfig() {
        ConfigurationCreateParams.Features.InvoiceHistory invoiceHistory = ConfigurationCreateParams.Features.InvoiceHistory.builder()
                .setEnabled(true)
                .build();
        ConfigurationCreateParams.Features.SubscriptionCancel cancelSubscription = ConfigurationCreateParams.Features.SubscriptionCancel.builder()
                .setEnabled(true)
                .setMode(ConfigurationCreateParams.Features.SubscriptionCancel.Mode.IMMEDIATELY)
                .build();
        ConfigurationCreateParams.Features.PaymentMethodUpdate paymentMethodUpdate = ConfigurationCreateParams.Features.PaymentMethodUpdate.builder()
                .setEnabled(true)
                .build();
        ConfigurationCreateParams.Features.SubscriptionUpdate subscriptionUpdate = ConfigurationCreateParams.Features.SubscriptionUpdate.builder()
                .setProducts(createProductList())
                .setDefaultAllowedUpdates(List.of(ConfigurationCreateParams.Features.SubscriptionUpdate.DefaultAllowedUpdate.PRICE))
                .setEnabled(true)
                .build();
        ConfigurationCreateParams.Features features = ConfigurationCreateParams.Features.builder()
                .setInvoiceHistory(invoiceHistory)
                .setSubscriptionCancel(cancelSubscription)
                .setPaymentMethodUpdate(paymentMethodUpdate)
                .setSubscriptionUpdate(subscriptionUpdate)
                .build();

        ConfigurationCreateParams params = ConfigurationCreateParams.builder().setFeatures(features).build();

        try {
            return Configuration.create(params);
        } catch (StripeException e) {
            throw new ATDException("Failed to create billing portal config.");
        }
    }

    private List<Product> createProductList() {
        List<Product> subscriptions = new ArrayList<>();
        for (SubscriptionType subscriptionType : SubscriptionType.values()) {
            Product product = Product.builder()
                    .setProduct(SubscriptionType.getProductId(subscriptionType))
                    .addPrice(subscriptionType.getValue())
                    .build();
            subscriptions.add(product);
        }

        return subscriptions;
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
