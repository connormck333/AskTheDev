package com.devconnor.askthedev.controllers;

import com.devconnor.askthedev.controllers.response.ATDUserResponse;
import com.devconnor.askthedev.models.UserAuthRequest;
import com.devconnor.askthedev.services.user.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<String> login(HttpServletResponse response, @RequestBody UserAuthRequest userAuthRequest) {
        return authenticationService.login(response, userAuthRequest.getEmail(), userAuthRequest.getPassword());
    }

    @PostMapping("/signup")
    public ResponseEntity<ATDUserResponse> registerUser(HttpServletResponse response, @RequestBody UserAuthRequest userAuthRequest) {
        return authenticationService.register(response, userAuthRequest.getEmail(), userAuthRequest.getPassword());
    }
}
