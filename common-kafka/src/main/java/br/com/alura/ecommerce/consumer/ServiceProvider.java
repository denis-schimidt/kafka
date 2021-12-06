package br.com.alura.ecommerce.consumer;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class ServiceProvider<T> implements Callable<Void> {
    private final Supplier<ConsumerService<T>> factory;

    public ServiceProvider(Supplier<ConsumerService<T>> factory) {
        this.factory = factory;
    }

    @Override
    public Void call() {
        ConsumerService<T> consumerService = factory.get();

        try (var service = new KafkaService(consumerService.getConsumerGroup(),
                consumerService.getTopic(),
                consumerService.getConsumerFunction(),
                Map.of())) {
            service.run();
        }

        return null;
    }
}
