package com.devconnor.askthedev.repositories;

import com.devconnor.askthedev.models.User;
import com.devconnor.askthedev.models.UserDTO;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<UserDTO> findUserById(UUID id);
    @NotNull Optional<User> findById(@NotNull UUID id);
    Optional<User> findUserByEmail(String email);
    @NotNull Optional<User> findByEmail(String email);
    Optional<UserDTO> findUserByCustomerId(String customerId);

    boolean existsUserByEmail(String email);
}
