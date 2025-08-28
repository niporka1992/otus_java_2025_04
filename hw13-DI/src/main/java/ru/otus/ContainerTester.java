package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.appcontainer.AppComponentsContainerImpl;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.config.AppConfig;
import ru.otus.config.EquationConfig;
import ru.otus.config.GameConfig;
import ru.otus.config.IOConfig;
import ru.otus.services.GameProcessor;

public final class ContainerTester {

    private static final Logger log = LoggerFactory.getLogger(ContainerTester.class);
    private static final String SEPARATOR = "\n\n\n";
    private static final String COMPONENT_CREATED_MSG = "Создан компонент GameProcessor: {}";

    // Приватный конструктор для утилитарного класса
    private ContainerTester() {
        throw new IllegalStateException("Utility class");
    }

    public static void testContainers() {
        log.info("Начало проверки работы нескольких вариантов конфигов ");

        log.info("Вариант 1: используем один конфигурационный класс AppConfig");
        AppComponentsContainer container1 = new AppComponentsContainerImpl(AppConfig.class);
        GameProcessor gameProcessor1 = container1.getAppComponent(GameProcessor.class);
        log.info(COMPONENT_CREATED_MSG, gameProcessor1.getClass().getSimpleName());

        log.info("Вариант 2: используем три конфигурационных класса: IOConfig, EquationConfig, GameConfig");
        AppComponentsContainer container2 =
                new AppComponentsContainerImpl(IOConfig.class, EquationConfig.class, GameConfig.class);
        GameProcessor gameProcessor2 = container2.getAppComponent("gameProcessor");
        log.info(COMPONENT_CREATED_MSG, gameProcessor2.getClass().getSimpleName());

        log.info("Вариант 3: автоматический поиск всех конфигураций в пакете ru.otus.config");
        AppComponentsContainer container3 = new AppComponentsContainerImpl("ru.otus.config");
        GameProcessor gameProcessor3 = container3.getAppComponent(GameProcessor.class);
        log.info(COMPONENT_CREATED_MSG, gameProcessor3.getClass().getSimpleName());

        log.info("Проверка завершена успешно {}", SEPARATOR);
    }
}
