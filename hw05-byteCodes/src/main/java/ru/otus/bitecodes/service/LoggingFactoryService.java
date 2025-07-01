package ru.otus.bitecodes.service;

import ru.otus.bitecodes.service.logging.TestLoggingService;

public interface LoggingFactoryService {
    TestLoggingService createLoggingService();
}
