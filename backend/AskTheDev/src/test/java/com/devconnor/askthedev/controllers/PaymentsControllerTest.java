package com.devconnor.askthedev.controllers;

import com.devconnor.askthedev.controllers.request.PaymentRequest;
import com.devconnor.askthedev.exception.UserNotFoundException;
import com.devconnor.askthedev.security.JwtUtil;
import com.devconnor.askthedev.security.SecurityConfig;
import com.devconnor.askthedev.services.payments.StripeService;
import com.devconnor.askthedev.utils.SubscriptionType;
import com.stripe.model.Event;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static com.devconnor.askthedev.utils.Utils.APPLICATION_JSON;
import static com.devconnor.askthedev.utils.Utils.convertToJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PaymentsController.class)
@Import({SecurityConfig.class})
@NoArgsConstructor
class PaymentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StripeService stripeService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @Mock
    private Event event;

    private static final String URL = "https://askthedev.com";
    private static final String STRIPE_SIGNATURE = "stripeSignature";
    private static final String STRIPE_PAYLOAD = "{ \"type\": \"payment_intent.succeeded\", \"data\": {\"object\": {}} }";

    @Test
    @WithMockUser
    void testCreateCheckout() throws Exception {
        PaymentRequest paymentRequest = createPaymentRequest();

        String body = convertToJson(paymentRequest);

        when(stripeService.createCheckoutSession(any(), eq(paymentRequest.getUserId()))).thenReturn(URL);

        mockMvc.perform(MockMvcRequestBuilders.post("/payment/create-checkout")
                        .contentType(APPLICATION_JSON)
                        .content(body)
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(URL));
    }

    @Test
    void testCreateCheckoutInvalidUser() throws Exception {
        PaymentRequest paymentRequest = createPaymentRequest();
        String body = convertToJson(paymentRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/payment/create-checkout")
                        .contentType(APPLICATION_JSON)
                        .content(body)
                        .with(csrf())
                )
                .andExpect(status().is(403));
    }

    @Test
    void testStripeEvent_Success() throws Exception {
        when(stripeService.validateAndRetrieveEvent(any(), any())).thenReturn(event);
        doNothing().when(stripeService).handleEvent(any(Event.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/payment/event")
                        .contentType(APPLICATION_JSON)
                        .content(STRIPE_PAYLOAD)
                        .header("Stripe-Signature", STRIPE_SIGNATURE)
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Purchase successful"));
    }

    @Test
    void testStripeEvent_Failure_MissingStripeSignature() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/payment/event")
                        .contentType(APPLICATION_JSON)
                        .content(STRIPE_PAYLOAD)
                        .with(csrf())
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void testStripeEvent_Failure_MissingEventPayload() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/payment/event")
                        .contentType(APPLICATION_JSON)
                        .header("Stripe-Signature", STRIPE_SIGNATURE)
                        .with(csrf())
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testManageSubscription_Success() throws Exception {
        UUID userId = UUID.randomUUID();

        when(stripeService.createBillingPortalSession(userId)).thenReturn(URL);

        mockMvc.perform(MockMvcRequestBuilders.get("/payment/manage-subscription")
                        .queryParam("userId", userId.toString())
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @Test
    void testManageSubscription_NotLoggedIn() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.get("/payment/manage-subscription")
                        .queryParam("userId", userId.toString())
                        .with(csrf())
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void testManageSubscription_MissingUserId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/payment/manage-subscription")
                        .with(csrf())
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testManageSubscription_UserNotFound() throws Exception {
        UUID userId = UUID.randomUUID();

        when(stripeService.createBillingPortalSession(userId)).thenThrow(UserNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/payment/manage-subscription")
                        .queryParam("userId", userId.toString())
                        .with(csrf())
                )
                .andExpect(status().isBadRequest());
    }

    private static PaymentRequest createPaymentRequest() {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setUserId(UUID.randomUUID());
        paymentRequest.setSubscriptionType(SubscriptionType.BASIC);

        return paymentRequest;
    }
}
