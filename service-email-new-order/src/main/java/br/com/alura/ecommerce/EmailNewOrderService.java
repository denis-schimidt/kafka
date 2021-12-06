package br.com.alura.ecommerce;

import br.com.alura.ecommerce.consumer.ConsumerFunction;
import br.com.alura.ecommerce.consumer.ConsumerService;
import br.com.alura.ecommerce.consumer.ServiceRunner;
import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;

import java.util.concurrent.ExecutionException;

public class EmailNewOrderService implements ConsumerService<Order> {

    public static void main(String[] args) {
        new ServiceRunner<>(EmailNewOrderService::new).start(1);
    }

    private void sendToDLQ(Order order, CorrelationId correlationId, String emailCode) {

        try(var deadLetterQueueDispatcher = new KafkaDispatcher<>()) {
            deadLetterQueueDispatcher.sendAsync("ecommerce.send.email.dead.letter.queue", order.getEmail(), correlationId, emailCode);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String getConsumerGroup() {
        return EmailNewOrderService.class.getSimpleName();
    }

    @Override
    public ConsumerFunction<Order> getConsumerFunction() {
        return (message) -> {
            System.out.println("------------------------------------------");
            System.out.println("Processing new order, preparing email");
            System.out.println(message.value());

            var order = message.value().getPayload();
            var correlationId = message.value().getCorrelationId().continueWith(EmailNewOrderService.class);

            var emailCode = "Thank you for your order! We are processing your order.";

            try (var emailDispatcher = new KafkaDispatcher<>()){
                emailDispatcher.send("ecommerce.send.email", order.getEmail(), correlationId, emailCode);

            } catch (ExecutionException | InterruptedException e) {
                sendToDLQ(order, correlationId, emailCode);
            }
        };
    }

    @Override
    public String getTopic() {
        return "ecommerce.new.order";
    }
}
