package com.devconnor.askthedev.services.payments;

import com.devconnor.askthedev.models.PendingEvent;
import com.devconnor.askthedev.repositories.PendingEventRepository;
import com.devconnor.askthedev.repositories.SubscriptionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class PendingEventProcessor {

    private final EventManager eventManager;
    private final PendingEventRepository pendingEventRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ObjectMapper objectMapper;

    private static final int TEN_MINUTES = 600000;

    @Scheduled(fixedDelay = TEN_MINUTES)
    public void processPendingEvents() {
        List<PendingEvent> pendingEvents = pendingEventRepository.findAll();

        for (PendingEvent pendingEvent : pendingEvents) {
            String subscriptionId = pendingEvent.getSubscriptionId();

            if (subscriptionRepository.findByStripeSubscriptionId(subscriptionId)) {
                try {
                    Event event = objectMapper.readValue(pendingEvent.getEventData(), Event.class);
                    eventManager.handleSuccessfulPayment(event);

                    pendingEventRepository.delete(pendingEvent);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
    }
}
