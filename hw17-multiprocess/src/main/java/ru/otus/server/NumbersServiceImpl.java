package ru.otus.server;

import io.grpc.stub.StreamObserver;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.numbers.NumberRequest;
import ru.otus.numbers.NumberResponse;
import ru.otus.numbers.NumbersServiceGrpc;

@SuppressWarnings("java:S2095")
public class NumbersServiceImpl extends NumbersServiceGrpc.NumbersServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(NumbersServiceImpl.class);
    private static final int SEND_INTERVAL_SECONDS = 2;

    @Override
    public void getNumber(NumberRequest request, StreamObserver<NumberResponse> responseObserver) {
        log.info(
                "request for the new sequence of numbers, firstValue:{}, lastValue:{}",
                request.getFirstValue(),
                request.getLastValue());

        var currentValue = new AtomicLong(request.getFirstValue());

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        try {
            Runnable task = () -> {
                long value = currentValue.incrementAndGet();
                var response = NumberResponse.newBuilder().setNumber(value).build();
                responseObserver.onNext(response);

                if (value >= request.getLastValue()) {
                    responseObserver.onCompleted();
                    log.info("sequence of numbers finished");
                    executor.shutdown();
                }
            };

            executor.scheduleAtFixedRate(task, 0, SEND_INTERVAL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("unexpected error in getNumber", e);
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
