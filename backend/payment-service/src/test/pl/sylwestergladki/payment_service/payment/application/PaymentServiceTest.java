package pl.sylwestergladki.payment_service.payment.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sylwestergladki.payment_service.messaging.event.OrderCreatedEvent;
import pl.sylwestergladki.payment_service.outbox.application.OutboxEventFactory;
import pl.sylwestergladki.payment_service.outbox.domain.OutboxEvent;
import pl.sylwestergladki.payment_service.outbox.domain.OutboxRepository;
import pl.sylwestergladki.payment_service.payment.application.dto.PaymentResponse;
import pl.sylwestergladki.payment_service.payment.application.exception.PaymentNotFoundException;
import pl.sylwestergladki.payment_service.payment.application.port.PaymentGateway;
import pl.sylwestergladki.payment_service.payment.domain.Payment;
import pl.sylwestergladki.payment_service.payment.domain.PaymentStatus;
import pl.sylwestergladki.payment_service.payment.infrastructure.persistance.PaymentRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private OutboxEventFactory outboxEventFactory;

    @Mock
    private PaymentGateway gateway;

    @InjectMocks
    private PaymentService paymentService;;

    private OrderCreatedEvent event(){
        return new OrderCreatedEvent(
                1L,
                BigDecimal.valueOf(100),
                "idem-key"
        );
    }

    @Test
    void shouldReturnEarly_whenPaymentAlreadyExists() {
//        given
        when(paymentRepository.existsByIdempotencyKey("idem-key"))
                .thenReturn(true);

//        when
        paymentService.process(event());

//        then
        verify(paymentRepository, never()).save(any());
        verify(outboxRepository, never()).save(any());
        verify(gateway, never()).charge();
    }



    @Test
    void shouldProcessSuccessFlow() throws Exception {

        when(paymentRepository.existsByIdempotencyKey("idem-key"))
                .thenReturn(false);

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> {
                    Payment payment = invocation.getArgument(0);
                    payment.setId(1L);
                    return payment;
                });

        when(outboxEventFactory.create(
                any(),
                any(),
                any(),
                any()
        )).thenReturn(new OutboxEvent());

        when(gateway.charge())
                .thenReturn(true);

        when(objectMapper.writeValueAsString(any()))
                .thenReturn("{json}");

        paymentService.process(event());

        verify(paymentRepository).save(any(Payment.class));
        verify(gateway).charge();
        verify(outboxRepository).save(any(OutboxEvent.class));
    }

    @Test
    void shouldProcessFailureFlow() throws Exception {

        when(paymentRepository.existsByIdempotencyKey("idem-key"))
                .thenReturn(false);

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> {
                    Payment payment = invocation.getArgument(0);
                    payment.setId(1L);
                    return payment;
                });

        when(outboxEventFactory.create(
                any(),
                any(),
                any(),
                any()
        )).thenReturn(new OutboxEvent());

        when(gateway.charge())
                .thenReturn(false);

        when(objectMapper.writeValueAsString(any()))
                .thenReturn("{json}");

        paymentService.process(event());

        verify(paymentRepository).save(any(Payment.class));
        verify(gateway).charge();
        verify(outboxRepository).save(any(OutboxEvent.class));
    }

    @Test
    void shouldReturnPaymentResponse_whenFound() {

        Payment payment = mock(Payment.class);

        when(payment.getId()).thenReturn(1L);
        when(payment.getOrderId()).thenReturn(10L);
        when(payment.getStatus()).thenReturn(PaymentStatus.SUCCESS);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getByIdOrThrow(1L);

        assertEquals(1L, response.id());
        assertEquals(10L, response.orderId());
        assertEquals(PaymentStatus.SUCCESS, response.status());
    }

    @Test
    void shouldThrowException_whenPaymentNotFound() {

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class,
                () -> paymentService.getByIdOrThrow(1L));
    }
}
