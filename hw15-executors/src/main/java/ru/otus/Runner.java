package ru.otus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Runner {
    private static final Logger log = LoggerFactory.getLogger(Runner.class);

    private final NumberSequence sequence;

    public Runner(NumberSequence sequence) {
        this.sequence = sequence;
    }

    public void run() {
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            executor.submit(sequence.createTask(true));
            executor.submit(sequence.createTask(false));
            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("✅ Все задачи завершены");
    }
}
