package com.devconnor.askthedev.services;

import com.devconnor.askthedev.models.Subscription;
import com.devconnor.askthedev.repositories.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public Subscription createSubscription(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    public Subscription getSubscriptionByUserId(Long userId) {
        return subscriptionRepository.getSubscriptionByUserId(userId);
    }
}
