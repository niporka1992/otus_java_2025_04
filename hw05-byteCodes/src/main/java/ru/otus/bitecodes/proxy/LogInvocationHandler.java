package ru.otus.bitecodes.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.bitecodes.annotation.Log;

public class LogInvocationHandler implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(LogInvocationHandler.class);

    private final Object target;
    private final Set<Method> methodsToLog = new HashSet<>();

    public LogInvocationHandler(Object target) {
        this.target = target;

        Class<?> targetClass = target.getClass();

        for (Class<?> interfaceClass : targetClass.getInterfaces()) {
            for (Method interfaceMethod : interfaceClass.getMethods()) {
                try {
                    Method implMethod =
                            targetClass.getMethod(interfaceMethod.getName(), interfaceMethod.getParameterTypes());
                    if (implMethod.isAnnotationPresent(Log.class)) {
                        methodsToLog.add(interfaceMethod);
                    }
                } catch (NoSuchMethodException ignored) {
                    // Метод из интерфейса не реализован явно в target — пропускаем
                }
            }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (logger.isInfoEnabled()) {
            Method targetMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
            if (methodsToLog.contains(targetMethod)) {
                StringBuilder sb = new StringBuilder("executed method: ").append(method.getName());
                if (args != null) {
                    for (int i = 0; i < args.length; i++) {
                        sb.append(", param").append(i + 1).append(": ").append(args[i]);
                    }
                }
                logger.info(sb.toString());
            }
        }
        return method.invoke(target, args);
    }
}
