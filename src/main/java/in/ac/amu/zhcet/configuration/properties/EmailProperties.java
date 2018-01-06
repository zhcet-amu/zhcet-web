package in.ac.amu.zhcet.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("zhcet.email")
public class EmailProperties {

    private boolean disabled;
    private String address;
    private String password;

}
