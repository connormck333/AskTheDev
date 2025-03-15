package com.devconnor.askthedev.services;

import com.devconnor.askthedev.models.ATDSubscription;
import com.devconnor.askthedev.repositories.SubscriptionRepository;
import com.devconnor.askthedev.utils.SubscriptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public ATDSubscription createSubscription(SubscriptionType subscriptionType, UUID userId) {

        return null;
    }

    public ATDSubscription getSubscriptionByUserId(UUID userId) {
        return subscriptionRepository.getSubscriptionByUserId(userId);
    }
}
