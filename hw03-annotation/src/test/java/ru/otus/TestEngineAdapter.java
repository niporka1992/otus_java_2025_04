package ru.otus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import ru.otus.annotations.After;
import ru.otus.annotations.Before;
import ru.otus.annotations.Test;

@SuppressWarnings("java:S3776")
class TestEngineAdapter {

    @TestFactory
    Collection<DynamicTest> dynamicTestsFromTestEngine() throws Exception {
        Class<?> testClass = Class.forName("ru.otus.ExampleTest");

        List<Method> beforeMethods = getAnnotatedMethods(testClass, Before.class);
        List<Method> afterMethods = getAnnotatedMethods(testClass, After.class);
        List<Method> testMethods = getAnnotatedMethods(testClass, Test.class);

        List<DynamicTest> dynamicTests = new ArrayList<>();
        for (Method testMethod : testMethods) {
            String displayName = getDisplayName(testMethod);
            dynamicTests.add(createDynamicTest(testClass, testMethod, beforeMethods, afterMethods, displayName));
        }

        return dynamicTests;
    }

    private List<Method> getAnnotatedMethods(
            Class<?> testClass, Class<? extends java.lang.annotation.Annotation> annotation) {
        List<Method> methods = new ArrayList<>();
        for (Method method : testClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                methods.add(method);
            }
        }
        return methods;
    }

    private String getDisplayName(Method testMethod) {
        Test testAnnotation = testMethod.getAnnotation(Test.class);
        String displayName = testAnnotation.displayName();
        return displayName.isEmpty() ? testMethod.getName() : displayName;
    }

    private DynamicTest createDynamicTest(
            Class<?> testClass,
            Method testMethod,
            List<Method> beforeMethods,
            List<Method> afterMethods,
            String displayName) {
        return DynamicTest.dynamicTest(displayName, () -> {
            Object instance = testClass.getDeclaredConstructor().newInstance();
            try {
                invokeMethods(beforeMethods, instance);
                invokeMethod(testMethod, instance);
            } finally {
                invokeMethodsWithCatch(afterMethods, instance);
            }
        });
    }

    private void invokeMethods(List<Method> methods, Object instance) throws Exception {
        for (Method method : methods) {
            method.setAccessible(true);
            method.invoke(instance);
        }
    }

    private void invokeMethod(Method method, Object instance) throws Exception {
        method.setAccessible(true);
        method.invoke(instance);
    }

    private void invokeMethodsWithCatch(List<Method> methods, Object instance) {
        for (Method method : methods) {
            method.setAccessible(true);
            try {
                method.invoke(instance);
            } catch (Exception e) {
                System.err.println("Ошибка в методе @After: " + e.getMessage());
            }
        }
    }
}
