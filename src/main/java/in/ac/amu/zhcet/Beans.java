package in.ac.amu.zhcet;

import com.j256.simplecsv.processor.CsvProcessor;
import in.ac.amu.zhcet.data.model.dto.upload.AttendanceUpload;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
