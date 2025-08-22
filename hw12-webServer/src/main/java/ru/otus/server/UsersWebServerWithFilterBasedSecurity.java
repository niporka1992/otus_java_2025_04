package ru.otus.server;

import com.google.gson.Gson;
import jakarta.servlet.DispatcherType;
import java.util.EnumSet;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Handler;
import ru.otus.dao.ClientDao;
import ru.otus.dao.UserDao;
import ru.otus.services.TemplateProcessor;
import ru.otus.services.UserAuthService;
import ru.otus.servlet.AuthorizationFilter;
import ru.otus.servlet.LoginServlet;

public class UsersWebServerWithFilterBasedSecurity extends UsersWebServerSimple {

    private static final String LOGIN_PATH = "/login";

    private final UserAuthService authService;

    public UsersWebServerWithFilterBasedSecurity(
            int port,
            UserAuthService authService,
            UserDao userDao,
            ClientDao clientDao,
            Gson gson,
            TemplateProcessor templateProcessor) {
        super(port, clientDao, userDao, gson, templateProcessor);
        this.authService = authService;
    }

    @Override
    protected Handler applySecurity(ServletContextHandler servletContextHandler, String... paths) {
        servletContextHandler.setAttribute("authService", authService);

        servletContextHandler.addServlet(
                new ServletHolder(new LoginServlet(templateProcessor, authService)), LOGIN_PATH);

        // ===== Фильтры авторизации =====
        for (String path : paths) {
            servletContextHandler.addFilter(AuthorizationFilter.class, path, EnumSet.of(DispatcherType.REQUEST));
        }

        return servletContextHandler;
    }
}
