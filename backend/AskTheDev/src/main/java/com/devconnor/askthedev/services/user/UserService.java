package com.devconnor.askthedev.services.user;

import com.devconnor.askthedev.exception.CustomerNotFoundException;
import com.devconnor.askthedev.exception.InvalidUserIdException;
import com.devconnor.askthedev.exception.UserNotFoundException;
import com.devconnor.askthedev.models.User;
import com.devconnor.askthedev.models.UserDTO;
import com.devconnor.askthedev.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserDTO getUserById(UUID userId) {
        Optional<UserDTO> optionalUser = userRepository.findUserById(userId);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        log.info("User with id {} not found", userId);
        throw new InvalidUserIdException(userId);
    }

    public User findById(UUID userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        log.info("User with id {} not found", userId);
        throw new InvalidUserIdException(userId);
    }

    public UserDTO getUserByEmail(String email) {
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        if (optionalUser.isPresent()) {
            return mapToDTO(optionalUser.get());
        }

        log.info("User with email {} not found", email);
        throw new UserNotFoundException(email);
    }

    public UserDTO getUserByCustomerId(String customerId) {
        Optional<UserDTO> optionalUser = userRepository.findUserByCustomerId(customerId);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        log.info("User with customer id {} not found", customerId);
        throw new CustomerNotFoundException();
    }

    private static UserDTO mapToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setCustomerId(user.getCustomerId());

        return userDTO;
    }
}
