package com.devconnor.askthedev.services.payments;

import com.devconnor.askthedev.exception.CustomerNotFoundException;
import com.devconnor.askthedev.exception.InvalidEventException;
import com.devconnor.askthedev.exception.SubscriptionNotFoundException;
import com.devconnor.askthedev.models.ATDSubscription;
import com.devconnor.askthedev.models.PendingEvent;
import com.devconnor.askthedev.models.UserDTO;
import com.devconnor.askthedev.repositories.PendingEventRepository;
import com.devconnor.askthedev.repositories.SubscriptionRepository;
import com.devconnor.askthedev.services.user.UserService;
import com.devconnor.askthedev.utils.SubscriptionType;
import com.stripe.Stripe;
import com.stripe.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.devconnor.askthedev.utils.Utils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventManagerTest {

    private EventManager eventManager;

    @Mock
    private UserService userService;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private PendingEventRepository pendingEventRepository;

    @Mock
    private Event mockedEvent;

    @Mock
    private EventDataObjectDeserializer eventDataObjectDeserializer;

    private MockedStatic<Subscription> mockedSubscription;

    @BeforeEach
    void setup() {
        Stripe.apiKey = "sk_test_dummyKey";
        eventManager = new EventManager(userService, subscriptionRepository, pendingEventRepository);
        mockedSubscription = mockStatic(Subscription.class);
    }

    @AfterEach
    void tearDown() {
        mockedSubscription.close();
    }

    @Test
    void testHandleSubscriptionCreation_Successful() {
        UUID userId = UUID.randomUUID();
        UserDTO user = createUserDTO(userId);
        Subscription subscription = createSubscription();

        when(mockedEvent.getDataObjectDeserializer()).thenReturn(eventDataObjectDeserializer);
        when(eventDataObjectDeserializer.getObject()).thenReturn(Optional.of(subscription));
        when(userService.getUserByCustomerId(CUSTOMER_ID)).thenReturn(user);

        eventManager.handleSubscriptionCreation(mockedEvent);

        ArgumentCaptor<ATDSubscription> subscriptionCaptor = ArgumentCaptor.forClass(ATDSubscription.class);
        verify(subscriptionRepository).save(subscriptionCaptor.capture());

        ATDSubscription atdSubscription = subscriptionCaptor.getValue();
        assertEquals(SUBSCRIPTION_ID, atdSubscription.getStripeSubscriptionId());
        assertEquals(userId, atdSubscription.getUserId());
        assertEquals(SubscriptionType.BASIC, atdSubscription.getType());
        assertEquals(ACTIVE, atdSubscription.getStatus());
        assertTrue(atdSubscription.isActive());
    }

    @Test
    void testHandleSubscriptionCreation_InvalidEventType() {
        Invoice invalidEventType = createInvoice();

        when(mockedEvent.getDataObjectDeserializer()).thenReturn(eventDataObjectDeserializer);
        when(eventDataObjectDeserializer.getObject()).thenReturn(Optional.of(invalidEventType));

        assertThrows(InvalidEventException.class, () -> eventManager.handleSubscriptionCreation(mockedEvent));
    }

    @Test
    void testHandleSubscriptionCreation_CustomerNotFound() {
        Subscription subscription = createSubscription();

        when(mockedEvent.getDataObjectDeserializer()).thenReturn(eventDataObjectDeserializer);
        when(eventDataObjectDeserializer.getObject()).thenReturn(Optional.of(subscription));
        when(userService.getUserByCustomerId(CUSTOMER_ID)).thenReturn(null);

        assertThrows(CustomerNotFoundException.class, () -> eventManager.handleSubscriptionCreation(mockedEvent));
    }

    @Test
    void testHandleSubscriptionUpdated_Successful_ChangedType() {
        Subscription subscription = createSubscription();
        UUID userId = UUID.randomUUID();
        ATDSubscription atdSubscription = createATDSubscription(userId);

        when(mockedEvent.getDataObjectDeserializer()).thenReturn(eventDataObjectDeserializer);
        when(eventDataObjectDeserializer.getObject()).thenReturn(Optional.of(subscription));
        when(subscriptionRepository.getSubscriptionByStripeSubscriptionId(SUBSCRIPTION_ID)).thenReturn(atdSubscription);

        eventManager.handleSubscriptionUpdated(mockedEvent);

        ArgumentCaptor<ATDSubscription> subscriptionCaptor = ArgumentCaptor.forClass(ATDSubscription.class);
        verify(subscriptionRepository).save(subscriptionCaptor.capture());

        ATDSubscription savedSubscription = subscriptionCaptor.getValue();

        assertEquals(SUBSCRIPTION_ID, savedSubscription.getStripeSubscriptionId());
        assertEquals(userId, savedSubscription.getUserId());
        assertEquals(SubscriptionType.BASIC, savedSubscription.getType());
        assertEquals(ACTIVE, savedSubscription.getStatus());
        assertTrue(savedSubscription.isActive());
    }

    @Test
    void testHandleSubscriptionUpdated_Successful_ChangedStatus() {
        Subscription subscription = createSubscription();
        subscription.setStatus(INACTIVE);
        UUID userId = UUID.randomUUID();
        ATDSubscription atdSubscription = createATDSubscription(userId);

        when(mockedEvent.getDataObjectDeserializer()).thenReturn(eventDataObjectDeserializer);
        when(eventDataObjectDeserializer.getObject()).thenReturn(Optional.of(subscription));
        when(subscriptionRepository.getSubscriptionByStripeSubscriptionId(SUBSCRIPTION_ID)).thenReturn(atdSubscription);

        eventManager.handleSubscriptionUpdated(mockedEvent);

        ArgumentCaptor<ATDSubscription> subscriptionCaptor = ArgumentCaptor.forClass(ATDSubscription.class);
        verify(subscriptionRepository).save(subscriptionCaptor.capture());

        ATDSubscription savedSubscription = subscriptionCaptor.getValue();

        assertEquals(SUBSCRIPTION_ID, savedSubscription.getStripeSubscriptionId());
        assertEquals(userId, savedSubscription.getUserId());
        assertEquals(SubscriptionType.BASIC, savedSubscription.getType());
        assertEquals(INACTIVE, savedSubscription.getStatus());
        assertFalse(savedSubscription.isActive());
    }

    @Test
    void testHandleSubscriptionUpdated_InvalidEventType() {
        Invoice invalidEventType = createInvoice();

        when(mockedEvent.getDataObjectDeserializer()).thenReturn(eventDataObjectDeserializer);
        when(eventDataObjectDeserializer.getObject()).thenReturn(Optional.of(invalidEventType));

        assertThrows(InvalidEventException.class, () -> eventManager.handleSubscriptionUpdated(mockedEvent));
    }

    @Test
    void testHandleSubscriptionUpdated_SubscriptionNotFound() {
        Subscription subscription = createSubscription();

        when(mockedEvent.getDataObjectDeserializer()).thenReturn(eventDataObjectDeserializer);
        when(eventDataObjectDeserializer.getObject()).thenReturn(Optional.of(subscription));
        when(subscriptionRepository.getSubscriptionByStripeSubscriptionId(SUBSCRIPTION_ID)).thenReturn(null);

        assertThrows(SubscriptionNotFoundException.class, () -> eventManager.handleSubscriptionUpdated(mockedEvent));
    }

    @Test
    void testHandleSubscriptionDeleted_Successful() {
        UUID userId = UUID.randomUUID();
        Subscription subscription = createSubscription();
        ATDSubscription atdSubscription = createATDSubscription(userId);

        when(mockedEvent.getDataObjectDeserializer()).thenReturn(eventDataObjectDeserializer);
        when(eventDataObjectDeserializer.getObject()).thenReturn(Optional.of(subscription));
        when(subscriptionRepository.getSubscriptionByStripeSubscriptionId(SUBSCRIPTION_ID)).thenReturn(atdSubscription);

        eventManager.handleSubscriptionDeleted(mockedEvent);

        verify(subscriptionRepository, times(1)).deleteByStripeSubscriptionId(SUBSCRIPTION_ID);
    }

    @Test
    void testHandleSubscriptionDeleted_InvalidEventType() {
        Invoice invalidEventType = createInvoice();

        when(mockedEvent.getDataObjectDeserializer()).thenReturn(eventDataObjectDeserializer);
        when(eventDataObjectDeserializer.getObject()).thenReturn(Optional.of(invalidEventType));

        assertThrows(InvalidEventException.class, () -> eventManager.handleSubscriptionDeleted(mockedEvent));
    }

    @Test
    void testHandleSubscriptionDeleted_SubscriptionNotFound() {
        Subscription subscription = createSubscription();

        when(mockedEvent.getDataObjectDeserializer()).thenReturn(eventDataObjectDeserializer);
        when(eventDataObjectDeserializer.getObject()).thenReturn(Optional.of(subscription));
        when(subscriptionRepository.getSubscriptionByStripeSubscriptionId(SUBSCRIPTION_ID)).thenReturn(null);

        assertThrows(SubscriptionNotFoundException.class, () -> eventManager.handleSubscriptionDeleted(mockedEvent));
    }

    @Test
    void testHandleSuccessfulPayment_Successful() {
        Invoice invoice = createInvoice();
        UUID userId = UUID.randomUUID();
        ATDSubscription atdSubscription = createATDSubscription(userId);

        when(mockedEvent.getDataObjectDeserializer()).thenReturn(eventDataObjectDeserializer);
        when(eventDataObjectDeserializer.getObject()).thenReturn(Optional.of(invoice));
        when(subscriptionRepository.getSubscriptionByStripeSubscriptionId(SUBSCRIPTION_ID)).thenReturn(atdSubscription);

        eventManager.handleSuccessfulPayment(mockedEvent);

        ArgumentCaptor<ATDSubscription> invoiceCaptor = ArgumentCaptor.forClass(ATDSubscription.class);
        verify(subscriptionRepository).save(invoiceCaptor.capture());

        ATDSubscription savedSubscription = invoiceCaptor.getValue();
        assertEquals(SUBSCRIPTION_ID, savedSubscription.getStripeSubscriptionId());
        assertEquals(userId, savedSubscription.getUserId());
        assertEquals(SubscriptionType.PRO, savedSubscription.getType());
        assertEquals(ACTIVE, savedSubscription.getStatus());
        assertTrue(savedSubscription.isActive());
    }

    @Test
    void testHandleSuccessfulPayment_Successful_SubscriptionNotFound() {
        Invoice invoice = createInvoice();

        when(mockedEvent.getDataObjectDeserializer()).thenReturn(eventDataObjectDeserializer);
        when(mockedEvent.getId()).thenReturn(EVENT_ID);
        when(mockedEvent.toJson()).thenReturn(EVENT_JSON);
        when(eventDataObjectDeserializer.getObject()).thenReturn(Optional.of(invoice));
        when(subscriptionRepository.getSubscriptionByStripeSubscriptionId(SUBSCRIPTION_ID)).thenReturn(null);

        eventManager.handleSuccessfulPayment(mockedEvent);

        ArgumentCaptor<PendingEvent> pendingEventCaptor = ArgumentCaptor.forClass(PendingEvent.class);
        verify(pendingEventRepository).save(pendingEventCaptor.capture());

        PendingEvent pendingEvent = pendingEventCaptor.getValue();
        assertEquals(SUBSCRIPTION_ID, pendingEvent.getSubscriptionId());
        assertEquals(EVENT_ID, pendingEvent.getEventId());
        assertNotNull(pendingEvent.getEventData());
    }

    @Test
    void testHandleSuccessfulPayment_InvalidEventType() {
        Subscription subscription = createSubscription();

        when(mockedEvent.getDataObjectDeserializer()).thenReturn(eventDataObjectDeserializer);
        when(eventDataObjectDeserializer.getObject()).thenReturn(Optional.of(subscription));

        assertThrows(InvalidEventException.class, () -> eventManager.handleSuccessfulPayment(mockedEvent));
    }

    @Test
    void testHandleFailedPayment_Successful() {
        Invoice invoice = createInvoice();
        Subscription subscription = createSubscription();
        subscription.setStatus(INACTIVE);
        UUID userId = UUID.randomUUID();
        ATDSubscription atdSubscription = createATDSubscription(userId);

        when(mockedEvent.getDataObjectDeserializer()).thenReturn(eventDataObjectDeserializer);
        when(eventDataObjectDeserializer.getObject()).thenReturn(Optional.of(invoice));
        when(subscriptionRepository.getSubscriptionByStripeSubscriptionId(SUBSCRIPTION_ID)).thenReturn(atdSubscription);
        mockedSubscription.when(() -> Subscription.retrieve(SUBSCRIPTION_ID)).thenReturn(subscription);

        eventManager.handleFailedPayment(mockedEvent);

        ArgumentCaptor<ATDSubscription> invoiceCaptor = ArgumentCaptor.forClass(ATDSubscription.class);
        verify(subscriptionRepository).save(invoiceCaptor.capture());

        ATDSubscription savedSubscription = invoiceCaptor.getValue();
        assertEquals(SUBSCRIPTION_ID, savedSubscription.getStripeSubscriptionId());
        assertEquals(userId, savedSubscription.getUserId());
        assertEquals(SubscriptionType.PRO, savedSubscription.getType());
        assertEquals(INACTIVE, savedSubscription.getStatus());
        assertFalse(savedSubscription.isActive());
    }

    @Test
    void testHandleFailedPayment_InvalidEventType() {
        Subscription subscription = createSubscription();

        when(mockedEvent.getDataObjectDeserializer()).thenReturn(eventDataObjectDeserializer);
        when(eventDataObjectDeserializer.getObject()).thenReturn(Optional.of(subscription));

        assertThrows(InvalidEventException.class, () -> eventManager.handleFailedPayment(mockedEvent));
    }

    @Test
    void testHandleFailedPayment_SubscriptionNotFound() {
        Invoice invoice = createInvoice();

        when(mockedEvent.getDataObjectDeserializer()).thenReturn(eventDataObjectDeserializer);
        when(eventDataObjectDeserializer.getObject()).thenReturn(Optional.of(invoice));
        when(subscriptionRepository.getSubscriptionByStripeSubscriptionId(SUBSCRIPTION_ID)).thenReturn(null);

        assertThrows(SubscriptionNotFoundException.class, () -> eventManager.handleFailedPayment(mockedEvent));
    }

    @Test
    void testHandleFailedPayment_FailedToGetSubscriptionStatus() {
        Invoice invoice = createInvoice();
        UUID userId = UUID.randomUUID();
        ATDSubscription atdSubscription = createATDSubscription(userId);

        when(mockedEvent.getDataObjectDeserializer()).thenReturn(eventDataObjectDeserializer);
        when(eventDataObjectDeserializer.getObject()).thenReturn(Optional.of(invoice));
        when(subscriptionRepository.getSubscriptionByStripeSubscriptionId(SUBSCRIPTION_ID)).thenReturn(atdSubscription);
        mockedSubscription.when(() -> Subscription.retrieve(SUBSCRIPTION_ID)).thenThrow(SubscriptionNotFoundException.class);

        assertThrows(SubscriptionNotFoundException.class, () -> eventManager.handleFailedPayment(mockedEvent));
    }

    @Test
    void testHandleFailedPayment_SubscriptionStatusNotFound() {
        Invoice invoice = createInvoice();
        UUID userId = UUID.randomUUID();
        ATDSubscription atdSubscription = createATDSubscription(userId);

        when(mockedEvent.getDataObjectDeserializer()).thenReturn(eventDataObjectDeserializer);
        when(eventDataObjectDeserializer.getObject()).thenReturn(Optional.of(invoice));
        when(subscriptionRepository.getSubscriptionByStripeSubscriptionId(SUBSCRIPTION_ID)).thenReturn(atdSubscription);
        mockedSubscription.when(() -> Subscription.retrieve(SUBSCRIPTION_ID)).thenReturn(null);

        assertThrows(SubscriptionNotFoundException.class, () -> eventManager.handleFailedPayment(mockedEvent));
    }

    private static Subscription createSubscription() {
        Subscription subscription = new Subscription();
        subscription.setId(SUBSCRIPTION_ID);
        subscription.setCustomer(CUSTOMER_ID);
        subscription.setStatus(ACTIVE);

        Price price = new Price();
        price.setCurrency("GBP");
        price.setProduct(SubscriptionType.BASIC.getValue());

        SubscriptionItem subscriptionItem = new SubscriptionItem();
        subscriptionItem.setPrice(price);

        SubscriptionItemCollection collection = new SubscriptionItemCollection();
        collection.setData(List.of(subscriptionItem));
        subscription.setItems(collection);

        return subscription;
    }

    private static Invoice createInvoice() {
        Invoice invoice = new Invoice();
        invoice.setSubscription(SUBSCRIPTION_ID);

        return invoice;
    }
}
