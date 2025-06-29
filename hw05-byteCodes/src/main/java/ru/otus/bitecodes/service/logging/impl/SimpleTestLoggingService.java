package ru.otus.bitecodes.service.logging.impl;

import ru.otus.bitecodes.annotation.Log;
import ru.otus.bitecodes.service.logging.TestLoggingService;

public class SimpleTestLoggingService implements TestLoggingService {

    public static SimpleTestLoggingService createInstance() {
        return new SimpleTestLoggingService();
    }

    private SimpleTestLoggingService() {}

    @Log
    @Override
    public void calculation(int param) {
        // Без реализации
    }

    @Log
    @Override
    public void calculation(int param1, int param2) {
        // Без реализации

    }

    @Log
    @Override
    public void calculation(int param1, String param3, int param2) {
        // Без реализации
    }

    @Override
    public void calculation(int param1, int param2, String param3) {
        // Без реализации
    }
}
