package com.devconnor.askthedev.services;

import com.devconnor.askthedev.controllers.response.ATDUserResponse;
import com.devconnor.askthedev.models.User;
import com.devconnor.askthedev.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public ResponseEntity<String> login(HttpServletRequest request, String email, String password) {
        Authentication authReq = UsernamePasswordAuthenticationToken
                .unauthenticated(email, password);
        Authentication authRes = authenticationManager.authenticate(authReq);

        SecurityContextHolder.getContext().setAuthentication(authRes);

        HttpSession session = request.getSession();
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return new ResponseEntity<>("User logged in.", HttpStatus.OK);
    }

    public ResponseEntity<ATDUserResponse> register(String email, String password) {
        if (userRepository.existsUserByEmail(email)) {
            ATDUserResponse atdUserResponse = new ATDUserResponse();
            atdUserResponse.setMessage("User already exists.");
            return new ResponseEntity<>(atdUserResponse, HttpStatus.CONFLICT);
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        User savedUser = userRepository.save(user);

        ATDUserResponse atdUserResponse = new ATDUserResponse();
        atdUserResponse.setUserId(savedUser.getId());
        atdUserResponse.setEmail(savedUser.getEmail());
        atdUserResponse.setMessage("User registered successfully.");

        return new ResponseEntity<>(atdUserResponse, HttpStatus.CREATED);
    }

    public boolean verifyUserSession(String email) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        String authenticatedEmail = auth.getName();
        return email.equals(authenticatedEmail);
    }
}
