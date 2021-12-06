package br.com.alura.ecommerce;

import br.com.alura.ecommerce.consumer.ConsumerFunction;
import br.com.alura.ecommerce.consumer.ConsumerService;
import br.com.alura.ecommerce.consumer.KafkaService;
import br.com.alura.ecommerce.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import static java.nio.file.StandardOpenOption.APPEND;

public class ReadingReportService implements ConsumerService<User> {

    public static void main(String[] args) {
        new ServiceRunner<>(ReadingReportService::new).start(3);
    }

    @Override
    public String getConsumerGroup() {
        return ReadingReportService.class.getSimpleName();
    }

    @Override
    public ConsumerFunction<User> getConsumerFunction() {

        return (message) -> {
            var user = message.value().getPayload();
            System.out.println("------------------------------------------");
            System.out.println("Processing report for " + user);

            try {
                var reportTemplatePath = Paths.get("src/main/resources", "report.txt");
                Path reportGeneratedPath = Paths.get("target", user.getUuid() + "-report.txt");
                Files.copy(reportTemplatePath, reportGeneratedPath, StandardCopyOption.REPLACE_EXISTING);
                Files.write(reportGeneratedPath, "Created for ".concat(user.getUuid()).getBytes(), APPEND);

                System.out.println("File created: " + reportGeneratedPath.toFile().getAbsolutePath());

            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    @Override
    public String getTopic() {
        return "ecommerce.user.report.requested";
    }
}
