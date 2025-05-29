package ru.otus;

import java.util.logging.Logger;
import ru.otus.annotations.After;
import ru.otus.annotations.Before;
import ru.otus.annotations.Test;

public class ExampleTest {

    private static final Logger logger = Logger.getLogger(ExampleTest.class.getName());

    private int counter;

    @Before
    public void setup() {
        counter = 0;
        logger.info("Setup completed");
    }

    @Test(displayName = "Тест сложения (не должно превышать 2)")
    public void testAddition() {
        counter += 3;
        logger.info(() -> String.format("Counter value after addition: %d", counter));
        if (counter > 2) {
            throw new TestFailedException("Counter exceeded threshold value");
        }
    }

    @Test(displayName = "Тест инкремента")
    public void testIncrement() {
        counter++;
        logger.info(() -> String.format("Counter value after increment: %d", counter));
    }

    @Test(displayName = "Тест декремента (значение не должно быть отрицательным)")
    public void testDecrement() {
        counter = 5;
        counter--;
        logger.info(() -> String.format("Counter value after decrement: %d", counter));
        if (counter < 0) {
            throw new TestFailedException("Counter went below zero");
        }
    }

    @After
    public void cleanup() {
        logger.info(() -> String.format("Final counter value: %d", counter));
    }

    private static class TestFailedException extends RuntimeException {
        TestFailedException(String message) {
            super(message);
        }
    }
}
