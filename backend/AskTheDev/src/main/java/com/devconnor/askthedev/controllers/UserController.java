package com.devconnor.askthedev.controllers;

import com.devconnor.askthedev.controllers.response.ATDUserResponse;
import com.devconnor.askthedev.models.User;
import com.devconnor.askthedev.security.JwtUtil;
import com.devconnor.askthedev.services.AuthenticationService;
import com.devconnor.askthedev.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.devconnor.askthedev.utils.Constants.INVALID_SESSION_MESSAGE;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final JwtUtil jwtUtil;

    @GetMapping("/{id}")
    public ResponseEntity<ATDUserResponse> getUserById(@RequestHeader("Authorization") String jwtToken, @PathVariable Long id) {
        ATDUserResponse atdUserResponse = new ATDUserResponse();
        User user = userService.getUserById(id);

        if (!jwtUtil.isTokenValid(jwtToken, user.getEmail())) {
            atdUserResponse.setMessage(INVALID_SESSION_MESSAGE);
            return new ResponseEntity<>(atdUserResponse, HttpStatus.UNAUTHORIZED);
        }

        atdUserResponse.setUser(user);

        return ResponseEntity.ok().body(atdUserResponse);
    }
}
