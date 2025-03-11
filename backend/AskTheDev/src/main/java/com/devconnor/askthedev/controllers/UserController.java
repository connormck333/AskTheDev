package com.devconnor.askthedev.controllers;

import com.devconnor.askthedev.controllers.response.ATDUserResponse;
import com.devconnor.askthedev.models.User;
import com.devconnor.askthedev.security.JwtUtil;
import com.devconnor.askthedev.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.devconnor.askthedev.utils.Constants.INVALID_SESSION_MESSAGE;
import static com.devconnor.askthedev.utils.Constants.USER_NOT_FOUND;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final JwtUtil jwtUtil;

    @GetMapping("/{id}")
    public ResponseEntity<ATDUserResponse> getUserById(
            HttpServletRequest request,
            @PathVariable Long id
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

        atdUserResponse.setUser(user);
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
