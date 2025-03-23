package com.devconnor.askthedev.controllers;

import com.devconnor.askthedev.controllers.response.ATDUserResponse;
import com.devconnor.askthedev.exception.UserNotFoundException;
import com.devconnor.askthedev.models.UserAuthRequest;
import com.devconnor.askthedev.security.JwtUtil;
import com.devconnor.askthedev.security.SecurityConfig;
import com.devconnor.askthedev.services.user.AuthenticationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthenticationController.class)
@Import({SecurityConfig.class})
@NoArgsConstructor
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    private static final String VALID_EMAIL = "test@gmail.com";
    private static final String VALID_PASSWORD = "password123";

    @Test
    void testRegister_WithValidEmailAndPassword() throws Exception {
        ATDUserResponse userResponse = generateUserResponse();
        when(authenticationService.register(any(HttpServletResponse.class), eq(VALID_EMAIL), eq(VALID_PASSWORD))).thenReturn(userResponse);

        String body = generateUserAuthRequest();

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(VALID_EMAIL))
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.activeSubscription").value(false))
                .andExpect(jsonPath("$.subscriptionType").doesNotExist());
    }

    @Test
    void testLogin_WithValidEmailAndPassword() throws Exception {
        when(authenticationService.login(any(HttpServletResponse.class), eq(VALID_EMAIL), eq(VALID_PASSWORD))).thenReturn(true);

        String userAuthRequest = generateUserAuthRequest();

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(userAuthRequest)
                )
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testLogout_Successful_WhenLoggedIn() throws Exception {
        doNothing().when(authenticationService).logout(any(HttpServletRequest.class), any(HttpServletResponse.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/logout"))
                .andExpect(status().isOk());
    }

    @Test
    void testLogout_WhenNotLoggedIn() throws Exception {
        doThrow(UserNotFoundException.class)
                .when(authenticationService).logout(any(HttpServletRequest.class), any(HttpServletResponse.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/logout"))
                .andExpect(status().isBadRequest());
    }

    private String generateUserAuthRequest() throws JsonProcessingException {
        UserAuthRequest userAuthRequest = new UserAuthRequest();
        userAuthRequest.setEmail(VALID_EMAIL);
        userAuthRequest.setPassword(VALID_PASSWORD);

        return convertToJson(userAuthRequest);
    }

    private ATDUserResponse generateUserResponse() {
        ATDUserResponse atdUserResponse = new ATDUserResponse();
        atdUserResponse.setEmail(VALID_EMAIL);
        atdUserResponse.setUserId(UUID.randomUUID());
        atdUserResponse.setActiveSubscription(false);

        return atdUserResponse;
    }
}
