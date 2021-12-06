package br.com.alura.ecommerce;

import br.com.alura.ecommerce.consumer.ConsumerFunction;
import br.com.alura.ecommerce.consumer.ConsumerService;
import br.com.alura.ecommerce.consumer.ServiceRunner;
import br.com.alura.ecommerce.database.LocalDatabase;
import br.com.alura.ecommerce.database.LocalDatabaseException;
import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import static java.lang.String.format;

public class FraudDetectorService implements ConsumerService<Order> {
    private final KafkaDispatcher<Order> kafkaDispatcher = new KafkaDispatcher<>();
    private final LocalDatabase localDatabase;

    public FraudDetectorService() {

        try {
            this.localDatabase = new LocalDatabase("service-fraud-detector/target", "frauds");
            this.localDatabase.executeStatement("CREATE TABLE IF NOT EXISTS fraud_analysis(order_id VARCHAR(200) primary key, is_fraud boolean NOT NULL)");

        }catch (SQLException e) {
            throw new LocalDatabaseException(e.getCause());
        }
    }

    public static void main(String[] args) {
        new ServiceRunner<>(FraudDetectorService::new).start(1);
    }

    @Override
    public String getConsumerGroup() {
        return FraudDetectorService.class.getSimpleName();
    }

    @Override
    public ConsumerFunction<Order> getConsumerFunction() {

        return (message) -> {
            System.out.println("------------------------------------------");
            System.out.println("Processing new order, checking for fraud");
            System.out.println(message.key());
            System.out.println(message.value());
            System.out.println(message.partition());
            System.out.println(message.offset());

            Order order = message.value().getPayload();

            if(wasProcessed(order)){
                System.out.printf("Order was processed before -> %s. Ignoring Fraud Analysis\n", order.getOrderId());
                return;
            }

            var currentCorrelationId = message.value().getCorrelationId().continueWith(FraudDetectorService.class);

            try {
                if (order.hasAmountGreaterThan(new BigDecimal("4500"))) {
                    localDatabase.save(format("INSERT INTO fraud_analysis(order_id, is_fraud) VALUES('%s',true)", order.getOrderId()));
                    System.out.println("Order Rejected for Fraud -> " + order);
                    kafkaDispatcher.send("ecommerce.order.rejected", order.getEmail(), currentCorrelationId, order);

                } else {
                    localDatabase.save(format("INSERT INTO fraud_analysis(order_id, is_fraud) VALUES('%s',false)", order.getOrderId()));
                    System.out.println("Order processed -> " + order);
                    kafkaDispatcher.send("ecommerce.order.approved", order.getEmail(), currentCorrelationId, order);
                }

            } catch (SQLException e) {
                throw new LocalDatabaseException(e);

            } catch (ExecutionException | InterruptedException e) {
                throw new KafkaSenderException(e.getCause());
            }
        } ;
    }

    private boolean wasProcessed(Order order) {
        try{
            return localDatabase.query("SELECT 1 FROM fraud_analysis WHERE order_id=?", order.getOrderId()).next();

        } catch (SQLException e) {
            throw new LocalDatabaseException(e.getCause());
        }
    }

    @Override
    public String getTopic() {
        return "ecommerce.new.order";
    }
}
