package ru.otus.bitecodes.service;

import java.lang.reflect.Proxy;
import ru.otus.bitecodes.proxy.LogInvocationHandler;
import ru.otus.bitecodes.service.logging.TestLoggingService;
import ru.otus.bitecodes.service.logging.impl.SimpleTestLoggingService;

public class SimpleLoggingFactoryService implements LoggingFactoryService {
    @Override
    public TestLoggingService createLoggingService() {
        var real = SimpleTestLoggingService.createInstance();
        return (TestLoggingService) Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                new Class<?>[] {TestLoggingService.class},
                new LogInvocationHandler(real));
    }
}
