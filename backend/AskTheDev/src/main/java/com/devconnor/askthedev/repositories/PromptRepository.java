package com.devconnor.askthedev.repositories;

import com.devconnor.askthedev.models.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromptRepository extends JpaRepository<Prompt, Long> {
}
