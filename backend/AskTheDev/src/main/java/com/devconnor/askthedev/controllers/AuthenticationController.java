package com.devconnor.askthedev.controllers;

import com.devconnor.askthedev.controllers.response.ATDUserResponse;
import com.devconnor.askthedev.exception.TermsNotAcceptedException;
import com.devconnor.askthedev.exception.UserNotFoundException;
import com.devconnor.askthedev.models.UserAuthRequest;
import com.devconnor.askthedev.services.user.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ATDUserResponse> login(HttpServletResponse response, @RequestBody UserAuthRequest userAuthRequest) {
        ATDUserResponse atdResponse = authenticationService.login(userAuthRequest.getEmail(), userAuthRequest.getPassword());
        if (atdResponse != null && atdResponse.getEmail() != null) {
            return ResponseEntity.ok(atdResponse);
        }

        throw new UserNotFoundException(userAuthRequest.getEmail());
    }

    @PostMapping("/signup")
    public ResponseEntity<ATDUserResponse> registerUser(HttpServletResponse response, @RequestBody UserAuthRequest userAuthRequest) {
        if (!userAuthRequest.isTermsAccepted()) {
            throw new TermsNotAcceptedException();
        }
        ATDUserResponse userResponse = authenticationService.register(userAuthRequest.getEmail(), userAuthRequest.getPassword());
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    public ResponseEntity<Boolean> logout(HttpServletRequest request, HttpServletResponse response) {
        authenticationService.logout(request, response);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
