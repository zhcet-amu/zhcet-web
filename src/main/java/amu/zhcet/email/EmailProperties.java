package amu.zhcet.email;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("zhcet.email")
public class EmailProperties {

    /**
     * Email Services are disabled in the project if set to true
     */
    private boolean disabled;

}
