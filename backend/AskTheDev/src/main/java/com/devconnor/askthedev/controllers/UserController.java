package com.devconnor.askthedev.controllers;

import com.devconnor.askthedev.controllers.response.ATDUserResponse;
import com.devconnor.askthedev.exception.InvalidSessionException;
import com.devconnor.askthedev.exception.UserNotFoundException;
import com.devconnor.askthedev.models.ATDSubscription;
import com.devconnor.askthedev.models.UserDTO;
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
        UserDTO user = userService.getUserById(id);
        try {
            validateUserSession(request, user);
        } catch (InvalidSessionException e) {
            ATDUserResponse response = new ATDUserResponse();
            response.setMessage(INVALID_SESSION_MESSAGE);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } catch (UserNotFoundException e) {
            ATDUserResponse response = new ATDUserResponse();
            response.setMessage(USER_NOT_FOUND);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        ATDUserResponse atdUserResponse = new ATDUserResponse();
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
        UserDTO user = userService.getUserByEmail(userEmail);

        if (user == null) {
            atdUserResponse.setMessage(USER_NOT_FOUND);
            return new ResponseEntity<>(atdUserResponse, HttpStatus.NOT_FOUND);
        }

        ATDSubscription atdSubscription = subscriptionService.getSubscriptionByUserId(user.getId());

        atdUserResponse.setUser(user);
        atdUserResponse.setActiveSubscription(atdSubscription != null && atdSubscription.isActive());
        if (atdSubscription != null) {
            atdUserResponse.setSubscriptionType(atdSubscription.getType());
        }

        return new ResponseEntity<>(atdUserResponse, HttpStatus.OK);
    }

    private void validateUserSession(HttpServletRequest request, UserDTO user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
        if (!jwtUtil.isSessionValid(request, user.getEmail())) {
            throw new InvalidSessionException();
        }
    }
}
