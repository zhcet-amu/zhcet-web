package in.ac.amu.zhcet.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("zhcet")
public class ApplicationProperties {
    private String url = "http://localhost:8080/";
}
