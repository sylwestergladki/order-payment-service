package pl.sylwestergladki.payment_service.outbox.domain;

public enum OutboxStatus {
    PENDING,
    PUBLISHED,
    DEAD_LETTER
}
