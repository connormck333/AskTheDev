package com.devconnor.askthedev.models;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "SUBSCRIPTIONS")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private SubscriptionTier tier;

    private Long joinTimestamp;
}
