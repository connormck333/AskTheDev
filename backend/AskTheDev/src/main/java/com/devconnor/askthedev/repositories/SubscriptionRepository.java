package com.devconnor.askthedev.repositories;

import com.devconnor.askthedev.models.ATDSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<ATDSubscription, Long> {
    ATDSubscription getSubscriptionByUserId(UUID userId);
    ATDSubscription getSubscriptionByStripeSubscriptionId(String stripeSubscriptionId);

    boolean findByStripeSubscriptionId(String stripeSubscriptionId);

    void deleteByStripeSubscriptionId(String stripeSubscriptionId);
}
