package br.com.alura.ecommerce;

import br.com.alura.ecommerce.consumer.ConsumerFunction;
import br.com.alura.ecommerce.consumer.ConsumerService;
import br.com.alura.ecommerce.consumer.ServiceRunner;
import br.com.alura.ecommerce.database.LocalDatabase;
import br.com.alura.ecommerce.database.LocalDatabaseException;

import java.sql.SQLException;
import java.util.UUID;

public class CreateUserService implements ConsumerService<Order> {
    private final LocalDatabase database;

    public CreateUserService() {

        try {
            this.database = new LocalDatabase("service-users/target", "users_database");
            this.database.executeStatement("CREATE TABLE IF NOT EXISTS users(uuid VARCHAR(200) primary key, email VARCHAR(200) NOT NULL UNIQUE)");

        } catch (SQLException e) {
            throw new LocalDatabaseException(e.getCause());
        }
    }

    public static void main(String[] args) {
        new ServiceRunner<Order>(CreateUserService::new).start(1);
    }

    private void insertNewUser(String email) throws SQLException {
        String uuid = UUID.randomUUID().toString();
        database.save("INSERT INTO users(uuid, email) VALUES(?,?)", uuid, email);

        System.out.println("-------------------------------------------------------");
        System.out.printf("User id: %s with email: %s successfully registered in the system%n", uuid, email);
    }

    private boolean isANewUser(String email) throws SQLException {
        return !database.query("SELECT 1 FROM users WHERE email = ?", email).next();
    }

    @Override
    public String getConsumerGroup() {
        return CreateUserService.class.getSimpleName();
    }

    @Override
    public ConsumerFunction<Order> getConsumerFunction() {

        return (message) -> {
            Order order = message.value().getPayload();

            try {
                if (isANewUser(order.getEmail())) {
                    insertNewUser(order.getEmail());
                }

            } catch (SQLException e) {
                throw new LocalDatabaseException(e.getCause());
            }
        };
    }

    @Override
    public String getTopic() {
        return "ecommerce.new.order";
    }
}
