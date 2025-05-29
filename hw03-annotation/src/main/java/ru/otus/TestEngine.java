package ru.otus;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.otus.annotations.After;
import ru.otus.annotations.Before;
import ru.otus.annotations.Test;

@SuppressWarnings({"java:S3776", "java:S1118", "java:S3011"})
public class TestEngine {

    private static final Logger logger = Logger.getLogger(TestEngine.class.getName());

    public static void run(String className) {
        try {
            Class<?> testClass = Class.forName(className);
            List<Method> beforeMethods = collectMethodsAnnotatedWith(testClass, Before.class);
            List<Method> testMethods = collectMethodsAnnotatedWith(testClass, Test.class);
            List<Method> afterMethods = collectMethodsAnnotatedWith(testClass, After.class);

            int passed = 0;
            int failed = 0;

            int testNumber = 1;

            for (Method testMethod : testMethods) {
                boolean result = runSingleTest(testClass, testMethod, beforeMethods, afterMethods, testNumber);
                if (result) {
                    passed++;
                } else {
                    failed++;
                }

                logger.info(
                        """

                        ====================================
                        ===          TEST FINISHED        ===
                        ====================================
                        """);

                testNumber++;
            }

            printSummary(testMethods.size(), passed, failed);

            logger.info(
                    """

                    ====================================
                    ===          ALL TESTS FINISHED   ===
                    ====================================
                    """);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to run TestEngine: {0}", e.getMessage());
        }
    }

    private static boolean runSingleTest(
            Class<?> testClass,
            Method testMethod,
            List<Method> beforeMethods,
            List<Method> afterMethods,
            int testNumber) {

        Object instance = null;
        try {
            instance = testClass.getDeclaredConstructor().newInstance();

            Test testAnnotation = testMethod.getAnnotation(Test.class);
            String displayName = testAnnotation.displayName();
            if (displayName == null || displayName.isEmpty()) {
                displayName = testMethod.getName();
            }

            for (Method before : beforeMethods) {
                before.setAccessible(true);
                before.invoke(instance);
            }

            testMethod.setAccessible(true);
            testMethod.invoke(instance);

            logger.log(Level.INFO, "Test #{0} passed: {1} ({2})", new Object[] {
                testNumber, testMethod.getName(), displayName
            });
            return true;

        } catch (Exception e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            Test testAnnotation = testMethod.getAnnotation(Test.class);
            String displayName = testAnnotation != null ? testAnnotation.displayName() : testMethod.getName();
            if (displayName == null || displayName.isEmpty()) {
                displayName = testMethod.getName();
            }

            logger.log(Level.WARNING, "Test #{0} failed: {1} ({2}) | Reason: {3}", new Object[] {
                testNumber, testMethod.getName(), displayName, cause
            });
            return false;

        } finally {
            if (instance != null) {
                for (Method after : afterMethods) {
                    try {
                        after.setAccessible(true);
                        after.invoke(instance);
                    } catch (Exception e) {
                        logger.warning("Error in @After method: " + e.getCause());
                    }
                }
            }
        }
    }

    private static List<Method> collectMethodsAnnotatedWith(Class<?> clazz, Class<? extends Annotation> annotation) {
        List<Method> result = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                result.add(method);
            }
        }
        return result;
    }

    private static void printSummary(int total, int passed, int failed) {
        logger.log(Level.INFO, "Total tests: {0}", total);
        logger.log(Level.INFO, "Passed: {0}", passed);
        logger.log(Level.INFO, "Failed: {0}", failed);
    }
}
