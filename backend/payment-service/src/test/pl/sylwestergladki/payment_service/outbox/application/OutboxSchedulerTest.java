package pl.sylwestergladki.payment_service.outbox.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxSchedulerTest {

    @Mock
    private OutboxBatchService outboxBatchService;

    @Mock
    private ExecutorService outboxExecutor;

    @InjectMocks
    private OutboxScheduler scheduler;

    private UUID eventId1;
    private UUID eventId2;

    @BeforeEach
    void setUp() {
        eventId1 = UUID.randomUUID();
        eventId2 = UUID.randomUUID();
    }

    @Test
    void shouldProcessAllEventsFromBatch() {

        when(outboxBatchService.fetchLockedBatch(anyInt()))
                .thenReturn(List.of(eventId1, eventId2));

        scheduler.processOutbox();

        verify(outboxBatchService).fetchLockedBatch(anyInt());

        verify(outboxExecutor, times(2))
                .submit(any(Runnable.class));
    }

    @Test
    void shouldNotProcessAnythingWhenBatchIsEmpty() {

        when(outboxBatchService.fetchLockedBatch(anyInt()))
                .thenReturn(List.of());

        scheduler.processOutbox();

        verify(outboxBatchService).fetchLockedBatch(anyInt());

        verifyNoInteractions(outboxExecutor);
    }

    @Test
    void shouldSubmitEachEventToExecutor() {

        when(outboxBatchService.fetchLockedBatch(anyInt()))
                .thenReturn(List.of(eventId1, eventId2));

        scheduler.processOutbox();

        verify(outboxExecutor, times(2))
                .submit(any(Runnable.class));
    }

    @Test
    void shouldStopWhenExecutorFails() {

        when(outboxBatchService.fetchLockedBatch(anyInt()))
                .thenReturn(List.of(eventId1, eventId2));

        doThrow(new RuntimeException("executor failure"))
                .when(outboxExecutor)
                .submit(any(Runnable.class));

        assertThrows(RuntimeException.class,
                () -> scheduler.processOutbox());

        verify(outboxBatchService).fetchLockedBatch(anyInt());
    }
}