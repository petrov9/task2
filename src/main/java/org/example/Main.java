package org.example;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) {

        List<Address> recipients = new ArrayList<>();
        recipients.add(new Address("d1", "n1"));
        recipients.add(new Address("d1", "n2"));
        recipients.add(new Address("d1", "n3"));
        recipients.add(new Address("d1", "n4"));
        recipients.add(new Address("d2", "n1"));
        recipients.add(new Address("d2", "n2"));
        recipients.add(new Address("d2", "n3"));
        recipients.add(new Address("d2", "n4"));

        Client client = new Client() {
            @Override
            public Event readData() {
                return new Event(recipients, new Payload("origin", UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8)));
            }

            @Override
            public Result sendData(Address dest, Payload payload) {

                double random = Math.random();

                if (random < 0.5) {
                    System.out.println("aceepted data to " + dest + ". payload: " + payload);
                    return Result.ACCEPTED;
                }

                System.out.println("rejected data to " + dest + ". payload: " + payload);
                return Result.REJECTED;
            }
        };

        AsyncHandler asyncHandler = new AsyncHandler(client);
        asyncHandler.performOperation();
//        asyncHandler.shutdown();
    }
}