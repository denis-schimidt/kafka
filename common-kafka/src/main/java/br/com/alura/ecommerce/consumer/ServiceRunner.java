package br.com.alura.ecommerce.consumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class ServiceRunner<T> {
    private final ServiceProvider<T> serviceProvider;

    public ServiceRunner(Supplier<ConsumerService<T>> factory) {
        this.serviceProvider = new ServiceProvider<>(factory);
    }

    public void start(int numberOfThreads) {
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        IntStream.range(0, numberOfThreads)
           .forEach(index -> executorService.submit(serviceProvider));
    }
}
