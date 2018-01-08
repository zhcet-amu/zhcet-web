package amu.zhcet.common.configuration;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MigrationStrategy implements FlywayMigrationStrategy {

    @Override
    public void migrate(Flyway flyway) {
        log.info("Applying migrations...");
        try {
            flyway.migrate();
        } catch (FlywayException f) {
            log.error("Attempting Repair...", f);
            flyway.repair();
        }
    }

}
