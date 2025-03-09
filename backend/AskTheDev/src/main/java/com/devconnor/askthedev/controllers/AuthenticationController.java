package com.devconnor.askthedev.controllers;

import com.devconnor.askthedev.controllers.response.ATDUserResponse;
import com.devconnor.askthedev.models.User;
import com.devconnor.askthedev.models.authentication.UserAuthRequest;
import com.devconnor.askthedev.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserAuthRequest userAuthRequest, HttpServletRequest request) {
        Authentication authReq = UsernamePasswordAuthenticationToken.unauthenticated(userAuthRequest.getEmail(), userAuthRequest.getPassword());
        Authentication authRes = authenticationManager.authenticate(authReq);

        SecurityContextHolder.getContext().setAuthentication(authRes);

        HttpSession session = request.getSession();
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return new ResponseEntity<>("User logged in.", HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<ATDUserResponse> registerUser(@RequestBody UserAuthRequest userAuthRequest, HttpServletRequest request) {
        if (userRepository.existsUserByEmail(userAuthRequest.getEmail())) {
            ATDUserResponse atdUserResponse = new ATDUserResponse();
            atdUserResponse.setMessage("User already exists.");
            return new ResponseEntity<>(atdUserResponse, HttpStatus.CONFLICT);
        }

        User user = new User();
        user.setEmail(userAuthRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userAuthRequest.getPassword()));

        User savedUser = userRepository.save(user);

        ATDUserResponse atdUserResponse = new ATDUserResponse();
        atdUserResponse.setUserId(savedUser.getId());
        atdUserResponse.setEmail(savedUser.getEmail());
        atdUserResponse.setMessage("User registered successfully.");

        return new ResponseEntity<>(atdUserResponse, HttpStatus.CREATED);
    }
}
