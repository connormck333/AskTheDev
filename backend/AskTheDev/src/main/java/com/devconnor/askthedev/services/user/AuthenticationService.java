package com.devconnor.askthedev.services.user;

import com.devconnor.askthedev.controllers.response.ATDUserResponse;
import com.devconnor.askthedev.models.User;
import com.devconnor.askthedev.repositories.UserRepository;
import com.devconnor.askthedev.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final UserService userService;

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    public ResponseEntity<String> login(HttpServletResponse response, String email, String password) {
        try {
//            Authentication authReq = UsernamePasswordAuthenticationToken
//                    .unauthenticated(email, password);
//            authenticationManager.authenticate(authReq);

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            jwtUtil.saveHttpCookie(response, email);

//        SecurityContextHolder.getContext().setAuthentication(authRes);
//
//        HttpSession session = request.getSession();
//        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            return ResponseEntity.ok("Login successful");
        } catch (AuthenticationException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<ATDUserResponse> register(HttpServletResponse response, String email, String password) {
        if (userRepository.existsUserByEmail(email)) {
            ATDUserResponse atdUserResponse = new ATDUserResponse();
            atdUserResponse.setMessage("User already exists.");
            return new ResponseEntity<>(atdUserResponse, HttpStatus.CONFLICT);
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        User savedUser = userRepository.save(user);

        jwtUtil.saveHttpCookie(response, email);

        ATDUserResponse atdUserResponse = new ATDUserResponse();
        atdUserResponse.setUserId(savedUser.getId());
        atdUserResponse.setEmail(savedUser.getEmail());
        atdUserResponse.setMessage("User registered successfully.");

        return new ResponseEntity<>(atdUserResponse, HttpStatus.CREATED);
    }
}
