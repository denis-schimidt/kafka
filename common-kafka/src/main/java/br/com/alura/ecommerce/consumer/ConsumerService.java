package br.com.alura.ecommerce.consumer;

public interface ConsumerService<T> {

    String getConsumerGroup();

    ConsumerFunction<T> getConsumerFunction();

    String getTopic();
}
