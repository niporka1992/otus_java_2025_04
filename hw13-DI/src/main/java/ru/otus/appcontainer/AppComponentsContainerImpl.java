package ru.otus.appcontainer;

import org.reflections.Reflections;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;
import ru.otus.appcontainer.exeption.ComponentException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        init(new Class<?>[]{initialConfigClass});
    }

    public AppComponentsContainerImpl(Class<?>... configClasses) {
        init(configClasses);
    }

    public AppComponentsContainerImpl(String basePackage) {
        Set<Class<?>> configClasses = findConfigClasses(basePackage);
        if (configClasses.isEmpty()) {
            throw new IllegalStateException(
                    "Конфигурационные классы не найдены в пакете: " + basePackage
            );
        }

        if (configClasses.size() > 1) {
            throw new IllegalStateException(
                    "Ожидался один конфигурационный класс, но найдено " + configClasses.size() +
                            ": " + configClasses
            );
        }
        init(configClasses.toArray(new Class<?>[0]));
    }

    private void init(Class<?>[] configClasses) {
        List<Class<?>> sortedConfigs = Arrays.stream(configClasses)
                .sorted(Comparator.comparingInt(
                        c -> c.getAnnotation(AppComponentsContainerConfig.class).order()))
                .toList();

        for (Class<?> configClass : sortedConfigs) {
            processConfig(configClass);
        }
    }

    private Set<Class<?>> findConfigClasses(String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> configClasses = reflections.getTypesAnnotatedWith(AppComponentsContainerConfig.class);

        if (configClasses.isEmpty()) {
            throw new ComponentException("Не найдены классы конфигурации в пакете: " + basePackage);
        }

        if (configClasses.size() > 1) {
            configClasses.removeIf(c -> c.equals(ru.otus.config.AppConfig.class));
        }

        return configClasses;
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);

        Object configInstance = createConfigInstance(configClass);

        List<Method> componentMethods = getComponentMethods(configClass);
        sortComponentMethods(componentMethods);

        for (Method method : componentMethods) {
            createAndStoreComponent(configInstance, method);
        }
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new ComponentException("Класс не является конфигурацией: " + configClass.getName());
        }
    }

    private Object createConfigInstance(Class<?> configClass) {
        try {
            return configClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException
                 | IllegalAccessException
                 | InvocationTargetException
                 | NoSuchMethodException e) {
            throw new ComponentException("Ошибка при создании экземпляра конфигурации: " + configClass.getName(), e);
        }
    }

    private List<Method> getComponentMethods(Class<?> configClass) {
        List<Method> componentMethods = new ArrayList<>();
        for (Method method : configClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(AppComponent.class)) {
                componentMethods.add(method);
            }
        }
        return componentMethods;
    }

    private void sortComponentMethods(List<Method> methods) {
        methods.sort(
                Comparator.comparingInt(m -> m.getAnnotation(AppComponent.class).order()));
    }

    private void createAndStoreComponent(Object configInstance, Method method) {
        AppComponent annotation = method.getAnnotation(AppComponent.class);
        String componentName = annotation.name();

        if (appComponentsByName.containsKey(componentName)) {
            throw new ComponentException("Компонент с именем уже существует: " + componentName);
        }

        Object[] args = resolveMethodDependencies(method);

        Object component;
        try {
            component = method.invoke(configInstance, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ComponentException("Ошибка при создании компонента: " + componentName, e);
        }

        appComponents.add(component);
        appComponentsByName.put(componentName, component);
    }

    private Object[] resolveMethodDependencies(Method method) {
        try {
            return Arrays.stream(method.getParameterTypes())
                    .map(this::getAppComponent)
                    .toArray();
        } catch (ComponentException e) {
            throw new ComponentException("Ошибка при разрешении зависимостей для компонента: " + method.getName(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getAppComponent(Class<C> componentClass) {
        List<Object> candidates = new ArrayList<>();

        for (Object component : appComponents) {
            if (componentClass.isAssignableFrom(component.getClass())) {
                candidates.add(component);
            }
        }

        if (candidates.isEmpty()) {
            throw new ComponentException("Компонент не найден для класса: " + componentClass.getName());
        }

        if (candidates.size() > 1) {
            throw new ComponentException("Найдено несколько компонентов для класса: " + componentClass.getName());
        }

        return (C) candidates.getFirst();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getAppComponent(String componentName) {
        Object component = appComponentsByName.get(componentName);

        if (component == null) {
            throw new ComponentException("Компонент не найден по имени: " + componentName);
        }

        return (C) component;
    }
}
