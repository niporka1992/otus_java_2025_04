package ru.otus;

import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cache.MyCache;
import ru.otus.core.repository.executor.DbExecutorImpl;
import ru.otus.core.sessionmanager.TransactionRunnerJdbc;
import ru.otus.crm.datasource.DriverManagerDataSource;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.crm.service.DBServiceClientCacheImpl;
import ru.otus.crm.service.DbServiceClientImpl;
import ru.otus.jdbc.impl.DataTemplateJdbc;
import ru.otus.jdbc.impl.EntityClassMetaDataImpl;
import ru.otus.jdbc.impl.EntitySQLMetaDataImpl;
import ru.otus.performance.PerformanceTester;

public class AppRunner {

    private static final Logger log = LoggerFactory.getLogger(AppRunner.class);
    private static final String URL = "jdbc:postgresql://localhost:5430/demoDB";
    private static final String USER = "usr";
    private static final String PASSWORD = "pwd";

    private static final String FORMAT = "%-22s | %-10d";

    private AppRunner() {
        throw new UnsupportedOperationException("Utility class");
    }

    @SuppressWarnings("java:S2629")
    public static void run() {
        DataSource dataSource = new DriverManagerDataSource(URL, USER, PASSWORD);
        FlywayMigrationExecutor.executeMigrations(dataSource);

        var transactionRunner = new TransactionRunnerJdbc(dataSource);
        var dbExecutor = new DbExecutorImpl();

        var clientMeta = new EntityClassMetaDataImpl<>(Client.class);
        var clientSql = new EntitySQLMetaDataImpl<Client>(clientMeta);
        var clientTemplate = new DataTemplateJdbc<>(dbExecutor, clientSql, clientMeta);

        DBServiceClient dbClientNoCache = new DbServiceClientImpl(transactionRunner, clientTemplate);
        DBServiceClient dbClientWithCache = new DBServiceClientCacheImpl(dbClientNoCache, new MyCache<>());

        log.info("----- Insert Test -----");
        long startInsertNoCache = System.currentTimeMillis();
        List<Long> idsNoCache = PerformanceTester.testInsert(dbClientNoCache);
        long endInsertNoCache = System.currentTimeMillis();

        long startInsertWithCache = System.currentTimeMillis();
        List<Long> idsWithCache = PerformanceTester.testInsert(dbClientWithCache);
        long endInsertWithCache = System.currentTimeMillis();

        log.info("----- Select Test -----");
        long startSelectNoCache = System.currentTimeMillis();
        PerformanceTester.testSelect(dbClientNoCache, idsNoCache);
        long endSelectNoCache = System.currentTimeMillis();

        long startSelectWithCache = System.currentTimeMillis();
        PerformanceTester.testSelect(dbClientWithCache, idsWithCache);
        long endSelectWithCache = System.currentTimeMillis();

        // Выводим сводку в лог
        log.info("\n===== Performance Summary (ms) =====");
        log.info(String.format("%-22s | %-10s", "Operation", "Duration"));
        log.info("------------------------|------------");
        log.info(String.format(FORMAT, "Insert (NO_CACHE)", endInsertNoCache - startInsertNoCache));
        log.info(String.format(FORMAT, "Insert (WITH_CACHE)", endInsertWithCache - startInsertWithCache));
        log.info(String.format(FORMAT, "Select (NO_CACHE)", endSelectNoCache - startSelectNoCache));
        log.info(String.format(FORMAT, "Select (WITH_CACHE)", endSelectWithCache - startSelectWithCache));
    }
}
