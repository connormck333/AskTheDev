package com.devconnor.askthedev.repositories;

import com.devconnor.askthedev.models.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PromptRepository extends JpaRepository<Prompt, Long> {

    @Query(value = """
        SELECT * FROM prompts
        WHERE web_url = :webUrl AND user_id = :userId
        ORDER BY created_at DESC
        LIMIT :limit OFFSET :offset
    """, nativeQuery = true)
    List<Prompt> findLatestPrompts(
            @Param("webUrl") String webUrl,
            @Param("userId") Long userId,
            @Param("limit") int limit,
            @Param("offset") int offset
    );
}
