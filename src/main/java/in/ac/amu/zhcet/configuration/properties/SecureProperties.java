package in.ac.amu.zhcet.configuration.properties;

import in.ac.amu.zhcet.utils.SecurityUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "zhcet.security")
public class SecureProperties {

    @Data
    public static class Roles {
        Map<String, List<String>> hierarchy = new LinkedHashMap<>();
    }

    private String pepper = SecurityUtils.getPepper();
    private Roles roles = new Roles();

}