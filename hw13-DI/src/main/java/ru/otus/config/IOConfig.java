package ru.otus.config;

import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;
import ru.otus.services.IOService;
import ru.otus.services.IOServiceStreams;

@AppComponentsContainerConfig(order = 0)
public class IOConfig {

    @SuppressWarnings("java:S106")
    @AppComponent(order = 0, name = "ioService")
    public IOService ioService() {
        return new IOServiceStreams(System.out, System.in);
    }
}
