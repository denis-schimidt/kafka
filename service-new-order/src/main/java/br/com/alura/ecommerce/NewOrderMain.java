package br.com.alura.ecommerce;

import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        try (var orderDispatcher = new KafkaDispatcher<Order>(); var emailDispatcher = new KafkaDispatcher<String>()) {
            var email = (Math.random() * 1000) + "@gmail.com";

            for (var i = 0; i < 10; i++) {
                var amount = new BigDecimal(Math.random() * 5000 + 1);
                var userId = UUID.randomUUID().toString();

                var order = new Order(userId, amount, email);
                orderDispatcher.send("ecommerce.new.order", userId, new CorrelationId(NewOrderMain.class, "ecommerce.new.order"), order);
            }
        }
    }
}
