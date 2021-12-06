package br.com.alura.ecommerce;

import br.com.alura.ecommerce.consumer.ConsumerFunction;
import br.com.alura.ecommerce.consumer.ConsumerService;
import br.com.alura.ecommerce.consumer.ServiceRunner;

public class EmailService implements ConsumerService<String> {

    public static void main(String[] args) {
        new ServiceRunner(EmailService::new).start(4);
    }

    @Override
    public String getConsumerGroup() {
        return EmailService.class.getSimpleName();
    }

    @Override
    public ConsumerFunction<String> getConsumerFunction() {
        return (record) -> {
            System.out.println("------------------------------------------");
            System.out.println("Send email");
            System.out.println(record.key());
            System.out.println(record.value());
            System.out.println(record.partition());
            System.out.println(record.offset());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Email sent");
        };
    }

    @Override
    public String getTopic() {
        return "ecommerce.send.email";
    }
}
