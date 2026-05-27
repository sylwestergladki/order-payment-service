package pl.sylwestergladki.payment_service.outbox.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sylwestergladki.payment_service.outbox.domain.OutboxRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class OutboxBatchServiceTest {

    @Mock
    private OutboxRepository outboxRepository;

    @InjectMocks
    private OutboxBatchService outboxBatchService;

    @Test
    void shouldLockBatch(){
        List<UUID> ids = List.of(UUID.randomUUID());

        when(outboxRepository.lockNextBatch(10))
                .thenReturn(ids);

        List<UUID> result = outboxBatchService.fetchLockedBatch(10);

        assertEquals(ids, result);

        verify(outboxRepository).lockNextBatch(10);
    }
}