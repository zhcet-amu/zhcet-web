package in.ac.amu.zhcet.configuration;

import in.ac.amu.zhcet.utils.SecurityUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "zhcet")
public class ApplicationProperties {

    private String salt = SecurityUtils.SALT;
    private String url = "http://localhost:8080/";
    private final Email email = new Email();
    private final Firebase firebase = new Firebase();

    @Data
    public static class Email {
        private String address;
        private String password;
    }

    @Data
    public static class Firebase {
        private String databaseUrl;
        private String storageBucket;
    }

}
