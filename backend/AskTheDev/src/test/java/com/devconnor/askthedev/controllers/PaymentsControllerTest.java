package com.devconnor.askthedev.controllers;

import com.devconnor.askthedev.controllers.request.PaymentRequest;
import com.devconnor.askthedev.security.JwtUtil;
import com.devconnor.askthedev.services.payments.StripeService;
import com.devconnor.askthedev.utils.SecurityTestConfig;
import com.devconnor.askthedev.utils.SubscriptionType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.Event;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static com.devconnor.askthedev.TestConstants.APPLICATION_JSON;
import static com.devconnor.askthedev.utils.Utils.convertToJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(PaymentsController.class)
@Import({SecurityTestConfig.class})
@NoArgsConstructor
public class PaymentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StripeService stripeService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Mock
    private Event event;

    private static final String STRIPE_SIGNATURE = "stripeSignature";
    private static final String STRIPE_PAYLOAD = "{ \"type\": \"payment_intent.succeeded\", \"data\": {\"object\": {}} }";

    @Test
    @WithMockUser
    public void testCreateCheckout() throws Exception {
        String url = "https://askthedev.com";
        PaymentRequest paymentRequest = createPaymentRequest();

        String body = convertToJson(paymentRequest);

        when(stripeService.createCheckoutSession(any(), eq(paymentRequest.getUserId()))).thenReturn(url);

        mockMvc.perform(MockMvcRequestBuilders.post("/payment/create-checkout")
                        .contentType(APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(url));
    }

    @Test
    public void testCreateCheckoutInvalidUser() throws Exception {
        PaymentRequest paymentRequest = createPaymentRequest();
        String body = convertToJson(paymentRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/payment/create-checkout")
                        .contentType(APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().is(403));
    }

    @Test
    public void testStripeEvent_Success() throws Exception {
        when(stripeService.validateAndRetrieveEvent(any(), any())).thenReturn(event);
        doNothing().when(stripeService).handleEvent(any(Event.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/payment/event")
                        .contentType(APPLICATION_JSON)
                        .content(STRIPE_PAYLOAD)
                        .header("Stripe-Signature", STRIPE_SIGNATURE)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Purchase successful"));
    }

    @Test
    public void testStripeEvent_Failure_MissingStripeSignature() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/payment/event")
                        .contentType(APPLICATION_JSON)
                        .content(STRIPE_PAYLOAD)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testStripeEvent_Failure_MissingEventPayload() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/payment/event")
                        .contentType(APPLICATION_JSON)
                        .header("Stripe-Signature", STRIPE_SIGNATURE)
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
