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
    /**
     * Email Address to be used to send emails
     */
    private String address;
    /**
     * Password of the email address to send emails from
     */
    private String password;

}
