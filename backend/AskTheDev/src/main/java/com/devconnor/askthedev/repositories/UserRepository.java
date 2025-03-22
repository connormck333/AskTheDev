package com.devconnor.askthedev.repositories;

import com.devconnor.askthedev.models.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findUserById(UUID id);
    @NotNull Optional<User> findById(@NotNull UUID id);
    Optional<User> findUserByEmail(String email);
    @NotNull Optional<User> findByEmail(String email);
    Optional<User> findUserByCustomerId(String customerId);

    boolean existsUserByEmail(String email);
}
