package ru.otus.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.dao.ClientDao;
import ru.otus.dao.ClientDaoImpl;
import ru.otus.dao.UserDao;
import ru.otus.dao.UserDaoImpl;
import ru.otus.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;
import ru.otus.model.User;
import ru.otus.server.UsersWebServer;
import ru.otus.server.UsersWebServerWithFilterBasedSecurity;
import ru.otus.services.TemplateProcessor;
import ru.otus.services.TemplateProcessorImpl;
import ru.otus.services.UserAuthService;
import ru.otus.services.UserAuthServiceImpl;

public class AppConfig {

    private static final String HIBERNATE_CFG = "hibernate.cfg.xml";
    private static final String TEMPLATES_DIR = "/templates/";
    private static final int WEB_SERVER_PORT = 4173;

    public SessionFactory sessionFactory() {
        Configuration configuration = new Configuration().configure(HIBERNATE_CFG);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        return HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class, User.class);
    }

    public Gson gson() {
        return new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    }

    public TemplateProcessor templateProcessor() {
        return new TemplateProcessorImpl(TEMPLATES_DIR);
    }

    public ClientDao clientDao(SessionFactory sessionFactory) {
        return new ClientDaoImpl(
                new TransactionManagerHibernate(sessionFactory), new DataTemplateHibernate<>(Client.class));
    }

    public UserDao userDao(SessionFactory sessionFactory) {
        return new UserDaoImpl(new TransactionManagerHibernate(sessionFactory));
    }

    public UserAuthService authService(UserDao userDao) {
        return new UserAuthServiceImpl(userDao);
    }

    public UsersWebServer usersWebServer() {
        SessionFactory sessionFactory = sessionFactory();
        ClientDao clientDao = clientDao(sessionFactory);
        UserDao userDao = userDao(sessionFactory);

        return new UsersWebServerWithFilterBasedSecurity(
                WEB_SERVER_PORT, authService(userDao), userDao, clientDao, gson(), templateProcessor());
    }
}
