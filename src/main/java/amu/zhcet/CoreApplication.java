package amu.zhcet;

import amu.zhcet.email.EmailProperties;
import amu.zhcet.firebase.FirebaseProperties;
import amu.zhcet.security.SecureProperties;
import amu.zhcet.storage.StorageProperties;
import io.sentry.Sentry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class)
@EnableConfigurationProperties({ApplicationProperties.class, SecureProperties.class,
        EmailProperties.class, StorageProperties.class, FirebaseProperties.class,
        StorageProperties.class})
public class CoreApplication {

    public static void main(String[] args) {
        Sentry.init();
        SpringApplication.run(CoreApplication.class, args);
    }

}
