package ru.otus.bitecodes.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.bitecodes.annotation.Log;

public class LogInvocationHandler implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(LogInvocationHandler.class);

    private final Object target;
    private final Map<Method, Method> methodCache = new HashMap<>();
    private final Map<Method, Boolean> logAnnotationCache = new HashMap<>();

    public LogInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method targetMethod = methodCache.computeIfAbsent(method, m -> {
            try {
                return target.getClass().getMethod(m.getName(), m.getParameterTypes());
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(e);
            }
        });

        Boolean shouldLog = logAnnotationCache.computeIfAbsent(targetMethod, m -> m.isAnnotationPresent(Log.class));

        if (Boolean.TRUE.equals(shouldLog) && logger.isInfoEnabled()) {
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
