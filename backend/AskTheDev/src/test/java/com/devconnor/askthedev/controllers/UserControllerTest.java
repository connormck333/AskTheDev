package com.devconnor.askthedev.controllers;

import com.devconnor.askthedev.security.JwtUtil;
import com.devconnor.askthedev.services.payments.SubscriptionService;
import com.devconnor.askthedev.services.user.UserService;
import com.devconnor.askthedev.utils.SecurityTestConfig;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@Import({SecurityTestConfig.class})
@NoArgsConstructor
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private SubscriptionService subscriptionService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    public void testGetUserById_Successful() throws Exception {
        UUID userId = UUID.randomUUID();

        when(jwtUtil.isSessionValid(any(HttpServletRequest.class), any(UUID.class))).thenReturn(true);

        
    }
}
