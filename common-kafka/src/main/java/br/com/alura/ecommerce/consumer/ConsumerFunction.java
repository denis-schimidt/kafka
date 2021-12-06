package br.com.alura.ecommerce.consumer;

import br.com.alura.ecommerce.KafkaMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerFunction<T> {
    void consume(ConsumerRecord<String, KafkaMessage<T>> record);
}
