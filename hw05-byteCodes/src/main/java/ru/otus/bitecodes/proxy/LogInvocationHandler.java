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

        for (Method method : target.getClass().getMethods()) {
            if (method.isAnnotationPresent(Log.class)) {
                methodsToLog.add(method);
            }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method targetMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());

        if (methodsToLog.contains(targetMethod) && logger.isInfoEnabled()) {
            StringBuilder sb = new StringBuilder("executed method: " + method.getName());
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    sb.append(", param").append(i + 1).append(": ").append(args[i]);
                }
            }
            logger.info(sb.toString());
        }

        return targetMethod.invoke(target, args);
    }
}
