package com.devconnor.askthedev.repositories;

import com.devconnor.askthedev.models.PendingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingEventRepository extends JpaRepository<PendingEvent, String> {
}
