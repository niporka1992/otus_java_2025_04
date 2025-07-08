package ru.otus;

import java.time.Clock;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.handler.ComplexProcessor;
import ru.otus.listener.ListenerPrinterConsole;
import ru.otus.model.Message;
import ru.otus.processor.EvenSecondExceptionProcessor;
import ru.otus.processor.ProcessorUpperField10;

public class HomeWork {
    private static final Logger logger = LoggerFactory.getLogger(HomeWork.class);

    public static void main(String[] args) {
        var processors =
                List.of(new EvenSecondExceptionProcessor(Clock.systemDefaultZone()), new ProcessorUpperField10());

        var complexProcessor = new ComplexProcessor(processors, ex -> logger.error("Exception in processor: ", ex));

        var listenerPrinter = new ListenerPrinterConsole();
        complexProcessor.addListener(listenerPrinter);

        var message = new Message.Builder(1L)
                .field1("test1")
                .field2("test2")
                .field10("hello")
                .build();

        try {
            var result = complexProcessor.handle(message);
            logger.info("result: {}", result);
        } catch (RuntimeException ex) {
            logger.warn("Processing aborted due to even second exception: {}", ex.getMessage());
        }

        complexProcessor.removeListener(listenerPrinter);
    }
}
