package pl.sylwestergladki.payment_service.outbox.domain;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {

    @Query(value = """
        SELECT id
        FROM outbox_events
        WHERE status = 'PENDING'
          AND next_retry_at <= now()
        ORDER BY created_at
        LIMIT :limit
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
    List<UUID> lockNextBatch(
            @Param("limit") int limit
    );

    @Query(value = """
        SELECT *
        FROM outbox_events
        WHERE id = :id
        FOR UPDATE
        """, nativeQuery = true)
    Optional<OutboxEvent> findByIdForUpdate(
            @Param("id") UUID id
    );
}
