package ru.otus;

import ru.otus.appcontainer.AppComponentsContainerImpl;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.services.GameProcessor;

@SuppressWarnings({"squid:S125", "squid:S106"})
public class App {

    public static void main(String[] args) {

        ContainerTester.testContainers();

        AppComponentsContainer container = new AppComponentsContainerImpl("ru.otus.config");
        GameProcessor gameProcessor = container.getAppComponent(GameProcessor.class);
        gameProcessor.startGame();
    }
}
