package amu.zhcet;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("zhcet")
public class ApplicationProperties {
    /**
     * URL of the website to be displayed in links and sent to emails.
     * This can be overridden by Admin at runtime
     */
    private String url = "http://localhost:8080/";
    /**
     * *disabled* Whether to convert resource URLs to their CDN versions.
     * When enabled and implemented, webjars and supported local development URLs will be converted to their CDN counterparts
     */
    private boolean useCdn;
}
