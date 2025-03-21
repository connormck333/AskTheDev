package com.devconnor.askthedev.security;

import com.devconnor.askthedev.controllers.AuthenticationController;
import com.devconnor.askthedev.controllers.PaymentsController;
import com.devconnor.askthedev.controllers.request.PaymentRequest;
import com.devconnor.askthedev.models.UserAuthRequest;
import com.devconnor.askthedev.utils.SubscriptionType;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static com.devconnor.askthedev.TestConstants.APPLICATION_JSON;
import static com.devconnor.askthedev.utils.Utils.convertToJson;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthenticationController.class)
@Import({SecurityConfig.class})
@NoArgsConstructor
class SecurityFilterChainTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationController authenticationController;

    @MockitoBean
    private PaymentsController paymentsController;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtUtil jwtUtil;

    @ParameterizedTest
    @ValueSource(strings = {"/auth/login", "/auth/signup"})
    void testLoginAndSignupPublicAccess(String endpoint) throws Exception {
        UserAuthRequest userAuthRequest = new UserAuthRequest();
        userAuthRequest.setEmail("test@gmail.com");
        userAuthRequest.setPassword("password");

        String body = convertToJson(userAuthRequest);

        mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                        .contentType(APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isOk());
    }

    @Test
    void testPaymentEventPublicAccess() throws Exception {
        String signature = "stripeSignature";
        String payload = "{ \"type\": \"payment_intent.succeeded\", \"data\": {\"object\": {}} }";

        mockMvc.perform(MockMvcRequestBuilders.post("/payment/event")
                        .header("Stripe-Signature", signature)
                        .contentType(APPLICATION_JSON)
                        .content(payload)
                )
                .andExpect(status().isOk());
    }

    @Test
    void testPrivateAccessEndpoint() throws Exception {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setUserId(UUID.randomUUID());
        paymentRequest.setSubscriptionType(SubscriptionType.BASIC);

        String body = convertToJson(paymentRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/payment/create-checkout")
                        .contentType(APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().is(403));
    }
}
