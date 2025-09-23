package ru.otus.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.config.ApplicationProperties;
import ru.otus.numbers.NumberRequest;
import ru.otus.numbers.NumbersServiceGrpc;

public class NumbersClient {
    private static final Logger log = LoggerFactory.getLogger(NumbersClient.class);
    private static final int LOOP_LIMIT = 50;
    private static final String CV_LOG = "currentValue:{}";

    public static void main(String[] args) {
        log.info("numbers Client is starting...");

        ManagedChannel channel = ManagedChannelBuilder.forAddress(
                        ApplicationProperties.getServerHost(), ApplicationProperties.getServerPort())
                .usePlaintext()
                .build();

        var asyncClient = NumbersServiceGrpc.newStub(channel);

        new NumbersClient().clientAction(asyncClient);

        log.info("numbers Client is shutting down...");
        channel.shutdown();
    }

    private void clientAction(NumbersServiceGrpc.NumbersServiceStub asyncClient) {
        long value = 0;
        var request = makeNumberRequest();
        var observer = new ClientStreamObserver();

        asyncClient.getNumber(request, observer);

        for (int i = 0; i < LOOP_LIMIT; i++) {
            long serverValue = observer.getLastValueAndReset();
            if (serverValue != 0) {
                value = value + serverValue + 1;
            } else {
                value++;
            }
            log.info(CV_LOG, value);
            sleepOneSec();
        }
    }

    private NumberRequest makeNumberRequest() {

        return NumberRequest.newBuilder().setFirstValue(1).setLastValue(30).build();
    }

    private static void sleepOneSec() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
