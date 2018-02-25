package amu.zhcet;

import amu.zhcet.core.admin.faculty.attendance.upload.AttendanceUpload;
import com.j256.simplecsv.processor.CsvProcessor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@Configuration
public class Beans {

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

    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster
                = new SimpleApplicationEventMulticaster();

        eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return eventMulticaster;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
