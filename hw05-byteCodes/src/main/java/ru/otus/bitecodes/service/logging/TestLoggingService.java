package ru.otus.bitecodes.service.logging;

public interface TestLoggingService {
    void calculation(int param);

    void calculation(int param1, int param2);

    void calculation(int param1, int param2, String param3);

    void calculation(int param1, String param2, int param3);
}
