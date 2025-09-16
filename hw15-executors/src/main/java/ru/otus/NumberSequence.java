package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumberSequence {
    private static final Logger log = LoggerFactory.getLogger(NumberSequence.class);
    private final Object monitor = new Object();

    private boolean turnFirst = true; // чей ход
    private int current; // текущее значение
    private int direction = FORWARD; // направление движения

    private final int max; // верхняя граница
    private final int min; // нижняя граница
    private final long delayMillis; // задержка между выводами

    private int printedThisRound = 0; // сколько потоков уже вывели текущее число
    private boolean running = true; // цикл активен
    private boolean goingDown = false; // признак, что мы уже шли вниз

    private static final int THREADS_COUNT = 2;
    private static final int FORWARD = 1;
    private static final int BACKWARD = -1;

    public NumberSequence(int min, int max, long delayMillis) {
        this.min = min;
        this.max = max;
        this.delayMillis = delayMillis;
        this.current = min;
    }

    public Runnable createTask(boolean isFirst) {
        return () -> {
            try {
                while (running) {
                    waitTurn(isFirst);
                    if (!running) {
                        break;
                    }
                    printAndAdvance(isFirst);
                    sleepDelay();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
    }

    private void waitTurn(boolean isFirst) throws InterruptedException {
        synchronized (monitor) {
            while (running && turnFirst != isFirst) {
                monitor.wait();
            }
        }
    }

    private void printAndAdvance(boolean isFirst) {
        synchronized (monitor) {
            if (!running) return;

            log.info("{}: {}", Thread.currentThread().getName(), current);

            printedThisRound++;
            if (printedThisRound == THREADS_COUNT) {
                if (goingDown && current == min) {
                    running = false;
                    monitor.notifyAll();
                    return;
                }
                if (current == max) {
                    direction = BACKWARD;
                    goingDown = true;
                } else if (current == min) {
                    direction = FORWARD;
                }
                advance();
                printedThisRound = 0;
            }

            turnFirst = !isFirst;
            monitor.notifyAll();
        }
    }

    private void sleepDelay() {
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void advance() {
        current += direction;
    }
}
