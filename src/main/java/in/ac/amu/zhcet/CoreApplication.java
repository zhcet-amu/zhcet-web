package in.ac.amu.zhcet;

import com.j256.simplecsv.processor.CsvProcessor;
import in.ac.amu.zhcet.data.model.dto.upload.AttendanceUpload;
import in.ac.amu.zhcet.service.storage.StorageProperties;
import in.ac.amu.zhcet.service.storage.StorageService;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Arrays;
import java.util.List;

@Slf4j
@ComponentScan
@EnableAsync
@EnableCaching
@SpringBootApplication
@AutoConfigurationPackage
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableConfigurationProperties(StorageProperties.class)
@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class)
public class CoreApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CoreApplication.class);
    }

    public static void main(String[] args) {
        Sentry.init();
        SpringApplication.run(CoreApplication.class, args);
    }

    @Bean("allowedCsvTypes")
    public List<String> allowedCsvTypes() {
        return Arrays.asList("text/csv", "application/vnd.ms-excel", "text/comma-separated-values");
    }

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public CsvProcessor<AttendanceUpload> csvProcessor() {
        return new CsvProcessor<>(AttendanceUpload.class)
                .withAlwaysTrimInput(true)
                .withIgnoreUnknownColumns(true)
                .withFlexibleOrder(true);
    }

    @Bean
    public HandlerExceptionResolver sentryExceptionResolver() {
        return new io.sentry.spring.SentryExceptionResolver();
    }

    @Bean
    public ServletContextInitializer sentryServletContextInitializer() {
        return new io.sentry.spring.SentryServletContextInitializer();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> storageService.init();
    }

}
