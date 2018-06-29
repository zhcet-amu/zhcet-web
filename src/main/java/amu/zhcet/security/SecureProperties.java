package amu.zhcet.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "zhcet.security")
public class SecureProperties {

    @Data
    public static class Roles {
        Map<String, List<String>> hierarchy = new LinkedHashMap<>();
    }

    /**
     * A predefined random secure string to be used for miscellaneous hashing in project.
     * This *must* be set for security purposes and *must* NOT be changed for the entire span of the project
     */
    private String pepper = CryptoUtils.getPepper();
    /**
     * The role hierarchy of the project
     */
    private Roles roles = new Roles();

}