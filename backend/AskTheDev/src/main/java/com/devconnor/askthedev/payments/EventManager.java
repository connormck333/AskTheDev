package com.devconnor.askthedev.payments;

import com.devconnor.askthedev.exception.CustomerNotFoundException;
import com.devconnor.askthedev.exception.InvalidEventException;
import com.devconnor.askthedev.exception.SubscriptionNotFoundException;
import com.devconnor.askthedev.models.ATDSubscription;
import com.devconnor.askthedev.models.PendingEvent;
import com.devconnor.askthedev.models.User;
import com.devconnor.askthedev.repositories.PendingEventRepository;
import com.devconnor.askthedev.repositories.SubscriptionRepository;
import com.devconnor.askthedev.services.user.UserService;
import com.devconnor.askthedev.utils.SubscriptionType;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventManager {

    private final UserService userService;
    private final SubscriptionRepository subscriptionRepository;
    private final PendingEventRepository pendingEventRepository;

    private static final String ACTIVE = "active";

    public void handleSubscriptionCreation(Event event) {
        Subscription subscription = (Subscription) deserializeEvent(event, Subscription.class);
        String customerId = getCustomerIdFromSubscription(subscription);

        SubscriptionItem subscriptionItem = subscription.getItems().getData().getFirst();
        String productId = subscriptionItem.getPrice().getProduct();
        SubscriptionType subscriptionType = SubscriptionType.valueOf(productId);

        User user = userService.getUserByCustomerId(customerId);

        ATDSubscription atdSubscription = new ATDSubscription();
        atdSubscription.setStripeSubscriptionId(subscription.getId());
        atdSubscription.setUserId(user.getId());
        atdSubscription.setType(subscriptionType);
        atdSubscription.setActive(subscription.getStatus().equalsIgnoreCase(ACTIVE));
        atdSubscription.setStatus(subscription.getStatus());

        subscriptionRepository.save(atdSubscription);
    }

    public void handleSubscriptionUpdated(Event event) {
        Subscription subscription = (Subscription) deserializeEvent(event, Subscription.class);

        SubscriptionItem subscriptionItem = subscription.getItems().getData().getFirst();
        String productId = subscriptionItem.getPrice().getProduct();
        SubscriptionType subscriptionType = SubscriptionType.valueOf(productId);

        // Update subscription type & status
        ATDSubscription atdSubscription = subscriptionRepository.getSubscriptionByStripeSubscriptionId(subscription.getId());
        atdSubscription.setType(subscriptionType);
        atdSubscription.setActive(subscription.getStatus().equalsIgnoreCase(ACTIVE));
        atdSubscription.setStatus(subscription.getStatus());

        subscriptionRepository.save(atdSubscription);
    }

    public void handleSubscriptionDeleted(Event event) {
        Subscription subscription = (Subscription) deserializeEvent(event, Subscription.class);
        String subscriptionId = subscription.getId();

        subscriptionRepository.deleteByStripeSubscriptionId(subscriptionId);
    }

    public void handleSuccessfulPayment(Event event) {
        Invoice invoice = (Invoice) deserializeEvent(event, Invoice.class);
        String subscriptionId = invoice.getSubscription();

        ATDSubscription atdSubscription = subscriptionRepository.getSubscriptionByStripeSubscriptionId(subscriptionId);
        if (atdSubscription == null) {
            subscriptionNotFound(event, subscriptionId);
            return;
        }

        atdSubscription.setActive(true);
        atdSubscription.setStatus(ACTIVE);

        subscriptionRepository.save(atdSubscription);
    }

    public void handleFailedPayment(Event event) {
        Invoice invoice = (Invoice) deserializeEvent(event, Invoice.class);
        String subscriptionId = invoice.getSubscription();

        ATDSubscription atdSubscription = subscriptionRepository.getSubscriptionByStripeSubscriptionId(subscriptionId);
        if (atdSubscription == null) {
            log.info("No subscription found by id {}", subscriptionId);
            return;
        }

        atdSubscription.setActive(false);
        atdSubscription.setStatus(getSubscriptionStatusById(subscriptionId));
        subscriptionRepository.save(atdSubscription);
    }

    private void subscriptionNotFound(Event event, String subscriptionId) {
        PendingEvent pendingEvent = new PendingEvent();
        pendingEvent.setSubscriptionId(subscriptionId);
        pendingEvent.setEventId(event.getId());
        pendingEvent.setEventData(event.toJson());

        pendingEventRepository.save(pendingEvent);
    }

    private Object deserializeEvent(Event event, Class<?> clazz) {
        return Optional.ofNullable(event.getDataObjectDeserializer().getObject())
                .map(clazz::cast)
                .orElseThrow(InvalidEventException::new);
    }

    private String getCustomerIdFromSubscription(Subscription subscription) {
        return Optional.ofNullable(subscription.getCustomer())
                .orElseThrow(CustomerNotFoundException::new);
    }

    private String getSubscriptionStatusById(String subscriptionId) {
        try {
            Subscription subscription = Subscription.retrieve(subscriptionId);
            return subscription.getStatus();
        } catch (StripeException e) {
            throw new SubscriptionNotFoundException();
        }
    }
}
