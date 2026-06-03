package pl.sylwestergladki.payment_service.outbox.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@AllArgsConstructor
@ConfigurationProperties(prefix = "outbox.retry")
public class OutboxRetryProperties {

    private int maxRetries;
    private long baseDelaySeconds;
    private long maxDelaySeconds;
    private double jitterFactor;
}