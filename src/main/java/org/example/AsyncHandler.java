package org.example;

import lombok.AllArgsConstructor;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@AllArgsConstructor
public class AsyncHandler implements Handler {

    private final Client client;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final AtomicBoolean running = new AtomicBoolean(true);

    @Override
    public Duration timeout() {
        return Duration.ofSeconds(1);
    }

    @Override
    public void performOperation() {
        while (running.get()) {
            Event event = client.readData();
            event.recipients().forEach(dest -> executor.submit(() -> sendWithRetry(dest, event.payload())));
        }
    }

    private void sendWithRetry(Address dest, Payload payload) {
        while (true) {
            Result result = client.sendData(dest, payload);
            if (result == Result.ACCEPTED) break;

            try {
                Thread.sleep(timeout().toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}