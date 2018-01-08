package amu.zhcet.firebase;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("zhcet.firebase")
public class FirebaseProperties {

    private boolean disabled;
    private String databaseUrl;
    private String storageBucket;
    private String messagingServerKey;

}
