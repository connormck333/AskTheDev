package com.devconnor.askthedev.models;

import com.devconnor.askthedev.utils.SubscriptionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "SUBSCRIPTIONS")
public class ATDSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String stripeSubscriptionId;

    private UUID userId;

    private SubscriptionType type;

    private boolean active;

    private String status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
