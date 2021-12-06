package br.com.alura.ecommerce.dispatcher;

import br.com.alura.ecommerce.CorrelationId;
import br.com.alura.ecommerce.KafkaMessage;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaDispatcher<T> implements Closeable {

    private final KafkaProducer<String, KafkaMessage<T>> producer;

    public KafkaDispatcher() {
        this.producer = new KafkaProducer<>(properties());
    }

    private static Properties properties() {
        var properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:29092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
        properties.setProperty(ProducerConfig.RETRIES_CONFIG, "5");
        return properties;
    }

    public Future<RecordMetadata> sendAsync(String topic, String key, CorrelationId correlationId, T payload) {
        System.out.println("Tentando enviar para o tópico: " + topic + " a mensagem -> " + payload);
        return sendMessage(topic, key, correlationId, payload);
    }

    public void send(String topic, String key, CorrelationId correlationId, T payload) throws ExecutionException, InterruptedException {
        System.out.println("Tentando enviar para o tópico: " + topic + " a mensagem -> " + payload);
        sendMessage(topic, key, correlationId, payload).get();
    }

    private Future<RecordMetadata> sendMessage(String topic, String key, CorrelationId correlationId, T payload) {
        var message = new KafkaMessage<>(correlationId.continueWith(topic), payload);
        var record = new ProducerRecord<>(topic, key, message);

        Callback callback = (data, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }
            System.out.println("sucesso enviando " + data.topic() + ":::partition " + data.partition() + "/ offset " + data.offset() + "/ timestamp " + data.timestamp());
        };

        return producer.send(record, callback);
    }

    @Override
    public void close() {
        producer.close();
    }
}
