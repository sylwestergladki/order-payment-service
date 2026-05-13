package pl.sylwestergladki.order_service.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import pl.sylwestergladki.order_service.kafka.event.PaymentFailedEvent;
import pl.sylwestergladki.order_service.kafka.event.PaymentSucceededEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(
            ProducerFactory<String, Object> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }


    @Bean
    public ConsumerFactory<String, PaymentSucceededEvent> paymentSucceededConsumerFactory() {
        return createFactory(PaymentSucceededEvent.class);
    }

    @Bean
    public ConsumerFactory<String, PaymentFailedEvent> paymentFailedConsumerFactory() {
        return createFactory(PaymentFailedEvent.class);
    }

    private <T> ConsumerFactory<String, T> createFactory(Class<T> type) {

        JsonDeserializer<T> deserializer = new JsonDeserializer<>(type, false);
        deserializer.setRemoveTypeHeaders(true);
        deserializer.setUseTypeHeaders(false);

        deserializer.addTrustedPackages("pl.sylwestergladki.order_service.kafka.event");

        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "order-group");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentSucceededEvent>
    paymentSucceededFactory(ConsumerFactory<String, PaymentSucceededEvent> cf) {

        var factory = new ConcurrentKafkaListenerContainerFactory<String, PaymentSucceededEvent>();
        factory.setConsumerFactory(cf);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentFailedEvent>
    paymentFailedFactory(ConsumerFactory<String, PaymentFailedEvent> cf) {

        var factory = new ConcurrentKafkaListenerContainerFactory<String, PaymentFailedEvent>();
        factory.setConsumerFactory(cf);
        return factory;
    }
}