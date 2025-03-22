package com.devconnor.askthedev.services.payments;

import com.devconnor.askthedev.exception.ATDException;
import com.devconnor.askthedev.exception.CustomerNotFoundException;
import com.devconnor.askthedev.exception.UserNotFoundException;
import com.devconnor.askthedev.models.User;
import com.devconnor.askthedev.repositories.UserRepository;
import com.devconnor.askthedev.services.user.UserService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.devconnor.askthedev.utils.Utils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripeServiceTest {

    private static final String STRIPE_SIGNATURE = "stripeSignature";
    private static final String EVENT_PAYLOAD = "eventPayload";
    private static final String CHECKOUT_URL = "checkoutUrl";
    private static final String PRICE_ID = "priceId";
    
    private StripeService stripeService;

    @Mock
    private UserService userService;

    @Mock
    private EventManager eventManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Event mockedEvent;

    @Mock
    private Customer mockedCustomer;

    @Mock
    private Session mockedSession;

    private MockedStatic<Webhook> mockedStaticWebhook;

    private MockedStatic<Customer> mockedStaticCustomer;

    private MockedStatic<Session> mockedStaticSession;

    @BeforeEach
    void setUp() {
        stripeService = new StripeService(userService, eventManager, userRepository);
        mockedStaticWebhook = mockStatic(Webhook.class);
        mockedStaticCustomer = mockStatic(Customer.class);
        mockedStaticSession = mockStatic(Session.class);
    }

    @AfterEach
    void tearDown() {
        mockedStaticWebhook.close();
        mockedStaticCustomer.close();
        mockedStaticSession.close();
    }

    @Test
    void testValidateAndRetrieveEvent_Successful() throws SignatureVerificationException {
        mockedStaticWebhook.when(() -> Webhook.constructEvent(eq(EVENT_PAYLOAD), eq(STRIPE_SIGNATURE), anyString())).thenReturn(mockedEvent);

        Event returnedEvent = stripeService.validateAndRetrieveEvent(STRIPE_SIGNATURE, EVENT_PAYLOAD);

        assertEquals(mockedEvent, returnedEvent);
    }

    @Test
    void testValidateAndRetrieveEvent_ThrowsException() {
        mockedStaticWebhook.when(() -> Webhook.constructEvent(eq(EVENT_PAYLOAD), eq(STRIPE_SIGNATURE), anyString())).thenThrow(SignatureVerificationException.class);

        assertThrows(SignatureVerificationException.class, () -> stripeService.validateAndRetrieveEvent(STRIPE_SIGNATURE, EVENT_PAYLOAD));
    }

    @Test
    void testCreateCheckoutSession_Successful_ExistingCustomer() {
        UUID userId = UUID.randomUUID();
        User user = createUser(userId);

        when(userService.findById(userId)).thenReturn(user);
        mockedStaticCustomer.when(() -> Customer.retrieve(eq(CUSTOMER_ID))).thenReturn(mockedCustomer);
        when(mockedCustomer.getId()).thenReturn(CUSTOMER_ID);
        mockedStaticSession.when(() -> Session.create(any(SessionCreateParams.class))).thenReturn(mockedSession);
        when(mockedSession.getUrl()).thenReturn(CHECKOUT_URL);

        String url = stripeService.createCheckoutSession(PRICE_ID, userId);

        assertEquals(CHECKOUT_URL, url);
    }

    @Test
    void testCreateCheckoutSession_Successful_NonExistingCustomer() {
        UUID userId = UUID.randomUUID();
        User user = createUser(userId);
        user.setCustomerId(null);

        when(userService.findById(userId)).thenReturn(user);
        mockedStaticCustomer.when(() -> Customer.create(any(CustomerCreateParams.class))).thenReturn(mockedCustomer);
        when(mockedCustomer.getId()).thenReturn(CUSTOMER_ID);
        mockedStaticSession.when(() -> Session.create(any(SessionCreateParams.class))).thenReturn(mockedSession);
        when(mockedSession.getUrl()).thenReturn(CHECKOUT_URL);

        String url = stripeService.createCheckoutSession(PRICE_ID, userId);

        assertEquals(CHECKOUT_URL, url);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testCreateCheckoutSession_UserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userService.findById(userId)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> stripeService.createCheckoutSession(PRICE_ID, userId));
    }

    @Test
    void testCreateCheckoutSession_CustomerNotFound() {
        UUID userId = UUID.randomUUID();
        User user = createUser(userId);

        when(userService.findById(userId)).thenReturn(user);
        mockedStaticCustomer.when(() -> Customer.retrieve(CUSTOMER_ID)).thenThrow(CustomerNotFoundException.class);

        assertThrows(CustomerNotFoundException.class, () -> stripeService.createCheckoutSession(PRICE_ID, userId));
    }

    @Test
    void testCreateCheckoutSession_CreateCustomerThrowsStripeException() {
        UUID userId = UUID.randomUUID();
        User user = createUser(userId);
        user.setCustomerId(null);

        when(userService.findById(userId)).thenReturn(user);
        mockedStaticCustomer.when(() -> Customer.create(any(CustomerCreateParams.class))).thenThrow(ATDException.class);

        assertThrows(ATDException.class, () -> stripeService.createCheckoutSession(PRICE_ID, userId));
    }

    @Test
    void testCreateCheckoutSession_CreateCheckoutSessionThrowsStripeException() {
        UUID userId = UUID.randomUUID();
        User user = createUser(userId);

        when(userService.findById(userId)).thenReturn(user);
        mockedStaticCustomer.when(() -> Customer.retrieve(eq(CUSTOMER_ID))).thenReturn(mockedCustomer);
        when(mockedCustomer.getId()).thenReturn(CUSTOMER_ID);
        mockedStaticSession.when(() -> Session.create(any(SessionCreateParams.class))).thenThrow(ATDException.class);

        assertThrows(ATDException.class, () -> stripeService.createCheckoutSession(PRICE_ID, userId));
    }
}
