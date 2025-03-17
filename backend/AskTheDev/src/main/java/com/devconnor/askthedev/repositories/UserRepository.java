package com.devconnor.askthedev.repositories;

import com.devconnor.askthedev.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByCustomerId(String customerId);

    boolean existsUserByEmail(String email);
}
