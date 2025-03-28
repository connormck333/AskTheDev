package com.devconnor.askthedev.services.payments;

import com.devconnor.askthedev.exception.CustomerNotFoundException;
import com.devconnor.askthedev.exception.InvalidEventException;
import com.devconnor.askthedev.exception.SubscriptionNotFoundException;
import com.devconnor.askthedev.models.ATDPayment;
import com.devconnor.askthedev.models.ATDSubscription;
import com.devconnor.askthedev.models.PendingEvent;
import com.devconnor.askthedev.models.UserDTO;
import com.devconnor.askthedev.repositories.PaymentRepository;
import com.devconnor.askthedev.repositories.PendingEventRepository;
import com.devconnor.askthedev.repositories.SubscriptionRepository;
import com.devconnor.askthedev.services.user.UserService;
import com.devconnor.askthedev.utils.SubscriptionType;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventManager {

    private final UserService userService;
    private final SubscriptionRepository subscriptionRepository;
    private final PendingEventRepository pendingEventRepository;
    private final PaymentRepository paymentRepository;

    private static final String ACTIVE = "active";

    public void handleSubscriptionCreation(Event event) {
        Subscription subscription = deserializeEvent(event, Subscription.class);
        String customerId = getCustomerIdFromSubscription(subscription);
        SubscriptionType subscriptionType = deriveSubscriptionType(subscription);

        UserDTO user = userService.getUserByCustomerId(customerId);
        if (user == null) {
            throw new CustomerNotFoundException();
        }

        ATDSubscription atdSubscription = new ATDSubscription();
        atdSubscription.setStripeSubscriptionId(subscription.getId());
        atdSubscription.setUserId(user.getId());
        atdSubscription.setType(subscriptionType);
        atdSubscription.setActive(subscription.getStatus().equalsIgnoreCase(ACTIVE));
        atdSubscription.setStatus(subscription.getStatus());

        subscriptionRepository.save(atdSubscription);
    }

    public void handleSubscriptionUpdated(Event event) {
        Subscription subscription = deserializeEvent(event, Subscription.class);
        SubscriptionType subscriptionType = deriveSubscriptionType(subscription);

        // Update subscription type & status
        ATDSubscription atdSubscription = subscriptionRepository.getSubscriptionByStripeSubscriptionId(subscription.getId());
        if (atdSubscription == null) {
            throw new SubscriptionNotFoundException();
        }

        atdSubscription.setType(subscriptionType);
        atdSubscription.setActive(subscription.getStatus().equalsIgnoreCase(ACTIVE));
        atdSubscription.setStatus(subscription.getStatus());

        subscriptionRepository.save(atdSubscription);
    }

    @Transactional
    public void handleSubscriptionDeleted(Event event) {
        Subscription subscription = deserializeEvent(event, Subscription.class);
        String subscriptionId = subscription.getId();

        ATDSubscription foundAtdSubscription = subscriptionRepository.getSubscriptionByStripeSubscriptionId(subscriptionId);
        if (foundAtdSubscription == null) {
            throw new SubscriptionNotFoundException();
        }

        subscriptionRepository.deleteByStripeSubscriptionId(subscriptionId);
    }

    public void handleSuccessfulPayment(Event event) {
        Invoice invoice = deserializeEvent(event, Invoice.class);
        String subscriptionId = invoice.getSubscription();

        ATDSubscription atdSubscription = subscriptionRepository.getSubscriptionByStripeSubscriptionId(subscriptionId);
        if (atdSubscription == null) {
            subscriptionNotFound(event, subscriptionId);
            return;
        }

        atdSubscription.setActive(true);
        atdSubscription.setStatus(ACTIVE);

        subscriptionRepository.save(atdSubscription);
        savePayment(invoice, atdSubscription.getUserId(), true);
    }

    public void handleFailedPayment(Event event) {
        Invoice invoice = deserializeEvent(event, Invoice.class);
        String subscriptionId = invoice.getSubscription();

        ATDSubscription atdSubscription = subscriptionRepository.getSubscriptionByStripeSubscriptionId(subscriptionId);
        if (atdSubscription == null) {
            log.info("No subscription found by id {}", subscriptionId);
            throw new SubscriptionNotFoundException();
        }

        atdSubscription.setActive(false);
        atdSubscription.setStatus(getSubscriptionStatusById(subscriptionId));
        subscriptionRepository.save(atdSubscription);
        savePayment(invoice, atdSubscription.getUserId(), false);
    }

    private void subscriptionNotFound(Event event, String subscriptionId) {
        PendingEvent pendingEvent = new PendingEvent();
        pendingEvent.setSubscriptionId(subscriptionId);
        pendingEvent.setEventId(event.getId());
        pendingEvent.setEventData(event.toJson());

        pendingEventRepository.save(pendingEvent);
    }

    private <T> T deserializeEvent(Event event, Class<T> clazz) {
        return event.getDataObjectDeserializer()
                .getObject()
                .filter(clazz::isInstance)
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
        } catch (StripeException | NullPointerException e) {
            throw new SubscriptionNotFoundException();
        }
    }

    private SubscriptionType deriveSubscriptionType(Subscription subscription) {
        SubscriptionItem subscriptionItem = subscription.getItems().getData().getFirst();
        String priceId = subscriptionItem.getPrice().getId();
        return SubscriptionType.fromString(priceId);
    }

    private void savePayment(Invoice invoice, UUID userId, boolean success) {
        ATDPayment atdPayment = new ATDPayment();
        atdPayment.setUserId(userId);
        atdPayment.setSuccess(success);
        atdPayment.setAmount(invoice.getAmountPaid());
        atdPayment.setStripeSubscriptionId(invoice.getSubscription());

        paymentRepository.save(atdPayment);
    }
}
