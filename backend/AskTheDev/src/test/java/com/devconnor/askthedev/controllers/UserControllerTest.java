package com.devconnor.askthedev.controllers;

import com.devconnor.askthedev.controllers.response.ATDUserResponse;
import com.devconnor.askthedev.models.UserDTO;
import com.devconnor.askthedev.repositories.SubscriptionRepository;
import com.devconnor.askthedev.security.JwtUtil;
import com.devconnor.askthedev.security.SecurityConfig;
import com.devconnor.askthedev.services.user.UserService;
import com.devconnor.askthedev.utils.SubscriptionType;
import jakarta.servlet.http.HttpServletRequest;
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

import static com.devconnor.askthedev.utils.Utils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@Import({SecurityConfig.class})
@NoArgsConstructor
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private SubscriptionRepository subscriptionRepository;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @Test
    @WithMockUser
    void testGetUserById_Successful() throws Exception {
        UUID userId = UUID.randomUUID();
        UserDTO user = createUserDTO(userId);

        when(jwtUtil.isSessionValid(any(HttpServletRequest.class), eq(user.getEmail()))).thenReturn(true);
        when(userService.getUserById(any(UUID.class))).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/user/%s", userId))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.activeSubscription").value(false))
                .andExpect(jsonPath("$.subscriptionType").doesNotExist());
    }

    @Test
    void testGetUserById_NotLoggedIn() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/user/%s", userId))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().is(403));
    }

    @Test
    @WithMockUser
    void testGetUserById_Unauthorized() throws Exception {
        UUID userId = UUID.randomUUID();
        UserDTO user = createUserDTO(UUID.randomUUID());

        when(jwtUtil.isSessionValid(any(HttpServletRequest.class), eq(user.getEmail()))).thenReturn(false);
        when(userService.getUserById(any(UUID.class))).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/user/%s", userId))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.message").value("Invalid Session"));
    }

    @Test
    @WithMockUser
    void testGetUserById_UserNotFound() throws Exception {
        UUID userId = UUID.randomUUID();

        when(userService.getUserById(any(UUID.class))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/user/%s", userId))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User Not Found"));
    }

    @Test
    @WithMockUser
    void testGetUserById_InvalidUserId() throws Exception {
        String invalidUserId = "invalidUserId";

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/user/%s", invalidUserId))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testGetCurrentUser_Successful_ActiveSubscription() throws Exception {
        String token = "sessionToken";
        UUID userId = UUID.randomUUID();
        UserDTO user = createUserDTO(userId);
        ATDUserResponse atdUserResponse = generateUserResponse(userId);
        atdUserResponse.setActiveSubscription(true);
        atdUserResponse.setSubscriptionType(SubscriptionType.BASIC);

        when(jwtUtil.getTokenFromCookie(any(HttpServletRequest.class))).thenReturn(token);
        when(jwtUtil.extractUserEmail(token)).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(userService.getATDUserResponseByUser(user)).thenReturn(atdUserResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/current-user")
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.activeSubscription").value(true))
                .andExpect(jsonPath("$.subscriptionType").value(atdUserResponse.getSubscriptionType().toString()));
    }

    @Test
    @WithMockUser
    void testGetCurrentUser_Successful_NoSubscriptionFound() throws Exception {
        String token = "sessionToken";
        UUID userId = UUID.randomUUID();
        UserDTO user = createUserDTO(userId);
        ATDUserResponse atdUserResponse = generateUserResponse(userId);

        when(jwtUtil.getTokenFromCookie(any(HttpServletRequest.class))).thenReturn(token);
        when(jwtUtil.extractUserEmail(token)).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(userService.getATDUserResponseByUser(user)).thenReturn(atdUserResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/current-user")
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.activeSubscription").value(false))
                .andExpect(jsonPath("$.subscriptionType").doesNotExist());
    }

    @Test
    @WithMockUser
    void testGetCurrentUser_Successful_InactiveSubscription() throws Exception {
        String token = "sessionToken";
        UUID userId = UUID.randomUUID();
        UserDTO user = createUserDTO(userId);
        ATDUserResponse atdUserResponse = generateUserResponse(userId);
        atdUserResponse.setSubscriptionType(SubscriptionType.BASIC);

        when(jwtUtil.getTokenFromCookie(any(HttpServletRequest.class))).thenReturn(token);
        when(jwtUtil.extractUserEmail(token)).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(userService.getATDUserResponseByUser(user)).thenReturn(atdUserResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/current-user")
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.activeSubscription").value(false))
                .andExpect(jsonPath("$.subscriptionType").value(atdUserResponse.getSubscriptionType().toString()));
    }

    @Test
    void testGetCurrentUser_NotLoggedIn() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/current-user")
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().is(403));
    }

    @Test
    @WithMockUser
    void testGetCurrentUser_UserNotFound() throws Exception {
        String token = "sessionToken";
        UUID userId = UUID.randomUUID();
        UserDTO user = createUserDTO(userId);

        when(jwtUtil.getTokenFromCookie(any(HttpServletRequest.class))).thenReturn(token);
        when(jwtUtil.extractUserEmail(token)).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/current-user")
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User Not Found"));
    }
}
