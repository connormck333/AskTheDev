package com.devconnor.askthedev.controllers;

import com.devconnor.askthedev.controllers.response.ATDUserResponse;
import com.devconnor.askthedev.models.ATDSubscription;
import com.devconnor.askthedev.models.User;
import com.devconnor.askthedev.security.JwtUtil;
import com.devconnor.askthedev.services.payments.SubscriptionService;
import com.devconnor.askthedev.services.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.devconnor.askthedev.utils.Constants.INVALID_SESSION_MESSAGE;
import static com.devconnor.askthedev.utils.Constants.USER_NOT_FOUND;

@Slf4j

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final SubscriptionService subscriptionService;

    private final JwtUtil jwtUtil;

    @GetMapping("/{id}")
    public ResponseEntity<ATDUserResponse> getUserById(
            HttpServletRequest request,
            @PathVariable UUID id
    ) {
        User user = userService.getUserById(id);
        ATDUserResponse atdUserResponse = validateUserSession(request, user);
        if (atdUserResponse != null) {
            return new ResponseEntity<>(atdUserResponse, HttpStatus.UNAUTHORIZED);
        }

        atdUserResponse = new ATDUserResponse();
        atdUserResponse.setUser(user);

        return ResponseEntity.ok().body(atdUserResponse);
    }

    @GetMapping("/current-user")
    public ResponseEntity<ATDUserResponse> getCurrentUser(
            HttpServletRequest request
    ) {
        ATDUserResponse atdUserResponse = new ATDUserResponse();
        String token = jwtUtil.getTokenFromCookie(request);
        if (token == null) {
            atdUserResponse.setMessage(USER_NOT_FOUND);
            return new ResponseEntity<>(atdUserResponse, HttpStatus.NOT_FOUND);
        }

        String userEmail = jwtUtil.extractUserEmail(token);
        User user = userService.getUserByEmail(userEmail);

        if (user == null) {
            atdUserResponse.setMessage(USER_NOT_FOUND);
            return new ResponseEntity<>(atdUserResponse, HttpStatus.NOT_FOUND);
        }

        ATDSubscription ATDSubscription = subscriptionService.getSubscriptionByUserId(user.getId());

        atdUserResponse.setUser(user);
        atdUserResponse.setActiveSubscription(ATDSubscription != null);
        if (ATDSubscription != null) {
            atdUserResponse.setSubscriptionType(ATDSubscription.getType());
        }

        return new ResponseEntity<>(atdUserResponse, HttpStatus.OK);
    }

    private ATDUserResponse validateUserSession(HttpServletRequest request, User user) {
        ATDUserResponse atdUserResponse = new ATDUserResponse();

        if (!jwtUtil.isSessionValid(request, user.getEmail())) {
            atdUserResponse.setMessage(INVALID_SESSION_MESSAGE);
            return atdUserResponse;
        }

        return null;
    }
}
