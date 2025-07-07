package ru.otus.bitecodes;

import ru.otus.bitecodes.service.LoggingFactoryService;
import ru.otus.bitecodes.service.SimpleLoggingFactoryService;
import ru.otus.bitecodes.service.logging.TestLoggingService;

public class Main {
    public static void main(String[] args) {
        // Создаём фабрику
        LoggingFactoryService factory = new SimpleLoggingFactoryService();

        // Получаем проксированный сервис
        TestLoggingService loggingService = factory.createLoggingService();

        // Вызываем методы
        loggingService.calculation(1);

        loggingService.calculation(1, 2);

        // Этот не помечен @Log
        loggingService.calculation(1, 2, "test");

        loggingService.calculation(1, "2", 3);
    }
}
