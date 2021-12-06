package br.com.alura.ecommerce;

import br.com.alura.ecommerce.consumer.KafkaService;
import br.com.alura.ecommerce.dispatcher.DeadLetterWriteException;
import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class BatchSendMessageService {
    public static final String JDBC_CONNECTION = "jdbc:sqlite:video5.3/ecommerce/service-users/target/users_database.db";

    private KafkaDispatcher<User> batchDispatcher = new KafkaDispatcher<>();

    public static void main(String[] args) {
        var batchService = new BatchSendMessageService();
        try (var service = new KafkaService<>(BatchSendMessageService.class.getSimpleName(),
                "ecommerce.all.users.requested",
                batchService::sendAllUsersToTopic,
                Map.of())) {
            service.run();
        }
    }

    private void sendAllUsersToTopic(ConsumerRecord<String, KafkaMessage<String>> message) {

        System.out.println("------------------------------------------");
        System.out.println("Selecting all users for topic: " + message.value());

        var currentCorrelationId = message.value().getCorrelationId().continueWith(BatchSendMessageService.class);

        getAllUsers().forEach(messageUser -> {

            try {
                var targetTopicName = message.value().getPayload();

                batchDispatcher.send(targetTopicName, messageUser.getUuid(), currentCorrelationId, new User(messageUser.getUuid()));

            } catch (Exception e) {
                e.printStackTrace();

                try {
                    batchDispatcher.send("ecommerce.all.users.requested.dead.letter.queue",
                            messageUser.getUuid(), currentCorrelationId.continueWith(BatchSendMessageService.class), messageUser);

                } catch (ExecutionException | InterruptedException ex) {
                    throw new DeadLetterWriteException(ex.getCause());
                }
            }
        });
    }

    private List<User> getAllUsers() {

        try {
            var connection = DriverManager.getConnection(JDBC_CONNECTION);
            var preparedStatement = connection.prepareStatement("SELECT uuid FROM users");

            ResultSet resultSet = preparedStatement.executeQuery();
            List<User> users = new ArrayList<>();

            while(resultSet.next()) {
               var user = new User(resultSet.getString(1));
                users.add(user);
            }

            return users;

        } catch (SQLException e) {
            throw  new RuntimeException(e.getCause());
        }
    }
}
