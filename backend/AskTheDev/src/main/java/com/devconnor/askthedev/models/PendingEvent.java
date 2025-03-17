package com.devconnor.askthedev.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "PENDING_EVENTS")
public class PendingEvent {
    @Id
    private String eventId;

    private String subscriptionId;

    @Column(columnDefinition = "TEXT")
    private String eventData;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
