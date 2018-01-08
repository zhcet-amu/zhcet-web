package amu.zhcet;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@ComponentScan
@SpringBootApplication
@AutoConfigurationPackage
@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class)
public class CoreApplication {

    public static void main(String[] args) {
        Sentry.init();
        SpringApplication.run(CoreApplication.class, args);
    }

}
