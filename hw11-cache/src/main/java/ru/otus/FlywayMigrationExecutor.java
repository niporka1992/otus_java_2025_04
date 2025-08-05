package ru.otus;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FlywayMigrationExecutor {

    private static final Logger log = LoggerFactory.getLogger(FlywayMigrationExecutor.class);

    private FlywayMigrationExecutor() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void executeMigrations(DataSource dataSource) {
        log.info("db migration started...");
        Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:/db/migration")
                .load()
                .migrate();
        log.info("db migration finished.");
    }
}
