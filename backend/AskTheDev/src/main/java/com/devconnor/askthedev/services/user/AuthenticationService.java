package com.devconnor.askthedev.services.user;

import com.devconnor.askthedev.controllers.response.ATDUserResponse;
import com.devconnor.askthedev.exception.ExistingUsernameException;
import com.devconnor.askthedev.exception.InvalidSessionException;
import com.devconnor.askthedev.exception.UserNotFoundException;
import com.devconnor.askthedev.models.RefreshToken;
import com.devconnor.askthedev.models.User;
import com.devconnor.askthedev.repositories.RefreshTokenRepository;
import com.devconnor.askthedev.repositories.UserRepository;
import com.devconnor.askthedev.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    public boolean login(HttpServletResponse response, String email, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            jwtUtil.saveHttpCookie(response, email);
            return true;
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    public ATDUserResponse register(HttpServletResponse response, String email, String password) {
        if (userRepository.existsUserByEmail(email)) {
            throw new ExistingUsernameException();
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

        return atdUserResponse;
    }

    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
             new SecurityContextLogoutHandler().logout(request, response, auth);

             String token = request.getHeader("Authorization");
             if (token != null && token.startsWith("Bearer ")) {
                 token = token.substring(7);
             } else {
                 throw new InvalidSessionException();
             }

             RefreshToken refreshToken = refreshTokenRepository.findByToken(token);
             if (refreshToken == null) {
                 throw new InvalidSessionException();
             }

             refreshToken.setActive(false);
             refreshTokenRepository.save(refreshToken);
        } else {
            throw new UserNotFoundException();
        }
    }
}
