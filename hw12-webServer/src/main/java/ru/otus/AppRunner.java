package ru.otus;

import ru.otus.config.AppConfig;
import ru.otus.server.UsersWebServer;

public class AppRunner {

    public void run() throws Exception {
        AppConfig config = new AppConfig();
        UsersWebServer webServer = config.usersWebServer();
        webServer.start();
        webServer.join();
    }
}
