package ru.otus.processor;

import java.time.Clock;
import java.time.LocalDateTime;
import ru.otus.model.Message;

@SuppressWarnings("java:S112")
public class EvenSecondExceptionProcessor implements Processor {

    private final Clock clock;

    public EvenSecondExceptionProcessor(Clock clock) {
        this.clock = clock;
    }

    @Override
    public Message process(Message message) {
        int second = LocalDateTime.now(clock).getSecond();
        if (second % 2 == 0) {
            throw new RuntimeException("Processing aborted: current second is even (" + second
                    + "). Processor works only on odd seconds.");
        }
        return message;
    }
}
