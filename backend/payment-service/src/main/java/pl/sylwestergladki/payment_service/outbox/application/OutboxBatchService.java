package pl.sylwestergladki.payment_service.outbox.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sylwestergladki.payment_service.outbox.domain.OutboxRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxBatchService {

    private final OutboxRepository repository;

    @Transactional
    public List<UUID> lockBatch(int size) {
        return repository.lockNextBatch(size);
    }
}
