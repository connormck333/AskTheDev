package com.devconnor.askthedev.services;

import com.devconnor.askthedev.models.User;
import com.devconnor.askthedev.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        log.info("User with id {} not found", userId);
        return null;
    }

    public User getUserByEmail(String email) {
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        log.info("User with email {} not found", email);
        return null;
    }
}
