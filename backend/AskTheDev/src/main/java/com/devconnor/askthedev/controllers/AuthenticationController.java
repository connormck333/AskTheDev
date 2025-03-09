package com.devconnor.askthedev.controllers;

import com.devconnor.askthedev.controllers.response.ATDUserResponse;
import com.devconnor.askthedev.models.UserAuthRequest;
import com.devconnor.askthedev.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(HttpServletRequest request, @RequestBody UserAuthRequest userAuthRequest) {
        return authenticationService.login(request, userAuthRequest.getEmail(), userAuthRequest.getPassword());
    }

    @PostMapping("/signup")
    public ResponseEntity<ATDUserResponse> registerUser(@RequestBody UserAuthRequest userAuthRequest) {
        return authenticationService.register(userAuthRequest.getEmail(), userAuthRequest.getPassword());
    }
}
