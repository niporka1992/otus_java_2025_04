package ru.otus.server;

import com.google.gson.Gson;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import ru.otus.dao.ClientDao;
import ru.otus.dao.UserDao;
import ru.otus.helpers.FileSystemHelper;
import ru.otus.services.TemplateProcessor;
import ru.otus.servlet.AuthCheckServlet;
import ru.otus.servlet.ClientsApiServlet;
import ru.otus.servlet.ClientsServlet;

public class UsersWebServerSimple implements UsersWebServer {

    // ==== Константы URL ====
    private static final String START_PAGE = "index.html";
    private static final String STATIC_DIR = "static";

    private static final String CLIENTS_UI_PATH = "/client";
    private static final String CLIENTS_API_PATH = "/api/client/*";
    private static final String AUTH_API_PATH = "/api/auth/*";

    private final ClientDao clientDao;

    @SuppressWarnings("unused")
    private final UserDao userDao;

    private final Gson gson;
    protected final TemplateProcessor templateProcessor;
    private final Server server;

    public UsersWebServerSimple(
            int port, ClientDao clientDao, UserDao userDao, Gson gson, TemplateProcessor templateProcessor) {
        this.clientDao = clientDao;
        this.userDao = userDao;
        this.gson = gson;
        this.templateProcessor = templateProcessor;
        this.server = new Server(port);
    }

    @Override
    public void start() throws Exception {
        if (server.getHandlers().isEmpty()) {
            initContext();
        }
        server.start();
    }

    @Override
    public void join() throws Exception {
        server.join();
    }

    private void initContext() {
        ResourceHandler resourceHandler = createResourceHandler();
        ServletContextHandler servletContextHandler = createServletContextHandler();

        Handler.Sequence sequence = new Handler.Sequence();
        sequence.addHandler(resourceHandler);
        sequence.addHandler(applySecurity(servletContextHandler, CLIENTS_UI_PATH, CLIENTS_API_PATH));

        server.setHandler(sequence);
    }

    @SuppressWarnings({"squid:S1172"})
    protected Handler applySecurity(ServletContextHandler servletContextHandler, String... paths) {
        return servletContextHandler;
    }

    private ResourceHandler createResourceHandler() {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirAllowed(false);
        resourceHandler.setWelcomeFiles(START_PAGE);
        resourceHandler.setBaseResourceAsString(FileSystemHelper.localFileNameOrResourceNameToFullPath(STATIC_DIR));
        return resourceHandler;
    }

    private ServletContextHandler createServletContextHandler() {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        // ===== UI =====
        context.addServlet(new ServletHolder(new ClientsServlet(templateProcessor)), CLIENTS_UI_PATH);

        // ===== API =====
        context.addServlet(new ServletHolder(new AuthCheckServlet(gson)), AUTH_API_PATH);
        context.addServlet(new ServletHolder(new ClientsApiServlet(clientDao, gson)), CLIENTS_API_PATH);

        return context;
    }
}
