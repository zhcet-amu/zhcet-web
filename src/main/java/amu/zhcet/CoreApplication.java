package amu.zhcet;

import amu.zhcet.email.EmailProperties;
import amu.zhcet.firebase.FirebaseProperties;
import amu.zhcet.security.SecureProperties;
import amu.zhcet.storage.StorageProperties;
import io.sentry.Sentry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;

import javax.servlet.DispatcherType;

@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class)
@EnableConfigurationProperties({ApplicationProperties.class, SecureProperties.class,
        EmailProperties.class, StorageProperties.class, FirebaseProperties.class})
public class CoreApplication {

    public static void main(String[] args) {
        Sentry.init();
        SpringApplication.run(CoreApplication.class, args);
    }

    // Workaround of issue https://github.com/spring-projects/spring-boot/issues/14897
    // Remove when fix is released
    @Bean
    public FilterRegistrationBean<ResourceUrlEncodingFilter> resourceUrlEncodingFilter() {
        FilterRegistrationBean<ResourceUrlEncodingFilter> registration = new FilterRegistrationBean<>(new ResourceUrlEncodingFilter());
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);
        return registration;
    }

}
