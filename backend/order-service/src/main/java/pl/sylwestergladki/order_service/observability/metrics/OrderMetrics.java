package pl.sylwestergladki.order_service.observability.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class OrderMetrics {

    private final MeterRegistry registry;

    private final Counter created;
    private final Counter succeeded;
    private final Counter failed;
    private final Timer createTimer;

    public OrderMetrics(MeterRegistry registry) {
        this.registry = registry;

        this.created = Counter.builder("order.created.total")
                .description("Orders created")
                .register(registry);

        this.succeeded = Counter.builder("order.succeeded.total")
                .register(registry);

        this.failed = Counter.builder("order.failed.total")
                .register(registry);

        this.createTimer = Timer.builder("order.create.time")
                .description("Time to create order")
                .register(registry);
    }

    public void created() {
        created.increment();
    }

    public void succeeded() {
        succeeded.increment();
    }

    public void failed() {
        failed.increment();
    }

    public Timer.Sample startCreateTimer() {
        return Timer.start(registry);
    }

    public void stopCreateTimer(Timer.Sample sample) {
        sample.stop(createTimer);
    }
}
