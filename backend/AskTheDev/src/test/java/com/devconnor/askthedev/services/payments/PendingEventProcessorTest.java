package com.devconnor.askthedev.services.payments;

import com.devconnor.askthedev.models.PendingEvent;
import com.devconnor.askthedev.repositories.PendingEventRepository;
import com.devconnor.askthedev.repositories.SubscriptionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PendingEventProcessorTest {

    private static final String EVENT_ID = "eventId";
    private static final String SUBSCRIPTION_ID = "subscriptionId";
    private static final String EVENT_JSON = "eventJson";
    
    private PendingEventProcessor pendingEventProcessor;

    @Mock
    private EventManager eventManager;

    @Mock
    private PendingEventRepository pendingEventRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Event mockedEvent;

    @BeforeEach
    void setUp() {
        pendingEventProcessor = new PendingEventProcessor(eventManager, pendingEventRepository, subscriptionRepository, objectMapper);
    }

    @Test
    void testProcessPendingEvents_Successful_NoPendingEvents() {
        when(pendingEventRepository.findAll()).thenReturn(List.of());

        pendingEventProcessor.processPendingEvents();

        verify(subscriptionRepository, times(0)).findByStripeSubscriptionId(anyString());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10})
    void testProcessPendingEvents_Successful_ManyPendingEvents(int amount) throws JsonProcessingException {
        List<PendingEvent> pendingEvents = createPendingEvents(amount);

        when(pendingEventRepository.findAll()).thenReturn(pendingEvents);
        when(subscriptionRepository.findByStripeSubscriptionId(SUBSCRIPTION_ID)).thenReturn(true);
        when(objectMapper.readValue(anyString(), eq(Event.class))).thenReturn(mockedEvent);
        doNothing().when(eventManager).handleSuccessfulPayment(any(Event.class));

        pendingEventProcessor.processPendingEvents();

        verify(subscriptionRepository, times(amount)).findByStripeSubscriptionId(anyString());
        verify(eventManager, times(amount)).handleSuccessfulPayment(any(Event.class));
        verify(pendingEventRepository, times(amount)).delete(any(PendingEvent.class));
    }

    @Test
    void testProcessingPendingEvents_SubscriptionNotFound() {
        List<PendingEvent> pendingEvents = createPendingEvents(1);

        when(pendingEventRepository.findAll()).thenReturn(pendingEvents);
        when(subscriptionRepository.findByStripeSubscriptionId(SUBSCRIPTION_ID)).thenReturn(false);

        pendingEventProcessor.processPendingEvents();

        verify(subscriptionRepository, times(1)).findByStripeSubscriptionId(anyString());
        verify(eventManager, times(0)).handleSuccessfulPayment(any(Event.class));
        verify(pendingEventRepository, times(0)).delete(any(PendingEvent.class));
    }

    private static List<PendingEvent> createPendingEvents(int amount) {
        List<PendingEvent> pendingEvents = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            PendingEvent pendingEvent = new PendingEvent();
            pendingEvent.setEventId(EVENT_ID);
            pendingEvent.setSubscriptionId(SUBSCRIPTION_ID);
            pendingEvent.setEventData(EVENT_JSON);

            pendingEvents.add(pendingEvent);
        }

        return pendingEvents;
    }
}
