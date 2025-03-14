package com.devconnor.askthedev.repositories;

import com.devconnor.askthedev.models.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Subscription getSubscriptionByUserId(Long userId);
}
