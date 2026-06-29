package pl.sylwestergladki.payment_service.payment.infrastructure.observability.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;
@Component
public class PaymentMetrics {
    private final MeterRegistry registry;

    private final Counter successCounter;
    private final Counter failedCounter;
    private final Timer processingTimer;
    private final Timer createTimer;

    public PaymentMetrics(MeterRegistry registry) {
        this.registry = registry;
        this.successCounter = Counter.builder("payments_success")
                .description("Successful payments")
                .register(registry);

        this.failedCounter = Counter.builder("payments_failed")
                .description("Failed payments")
                .register(registry);

        this.processingTimer = Timer.builder("payments_processing_time")
                .description("Payment processing time")
                .register(registry);

        this.createTimer = Timer.builder("order.create.time")
                .description("Time to create order")
                .register(registry);
    }

    public Counter success() {
        return successCounter;
    }

    public Counter failed() {
        return failedCounter;
    }

    public Timer timer() {
        return processingTimer;
    }

    public Timer.Sample startProcessingTimer() {
        return Timer.start(registry);
    }

    public void stopProcessingTimer(Timer.Sample sample) {
        sample.stop(processingTimer);
    }
}
