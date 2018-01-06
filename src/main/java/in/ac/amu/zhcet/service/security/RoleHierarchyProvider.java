package in.ac.amu.zhcet.service.security;

import in.ac.amu.zhcet.configuration.properties.SecureProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RoleHierarchyProvider {

    @Bean
    RoleHierarchy roleHierarchy(SecureProperties secureProperties) {
        Map<String, List<String>> roleHierarchyMapping = secureProperties.getRoles().getHierarchy();

        StringWriter roleHierarchyDescriptionBuffer = new StringWriter();
        PrintWriter roleHierarchyDescriptionWriter = new PrintWriter(roleHierarchyDescriptionBuffer);

        for (Map.Entry<String, List<String>> entry : roleHierarchyMapping.entrySet()) {

            String currentRole = entry.getKey();
            List<String> impliedRoles = entry.getValue();

            for (String impliedRole : impliedRoles) {
                String roleMapping = currentRole + " > " + impliedRole;
                roleHierarchyDescriptionWriter.println(roleMapping);
            }
        }

        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy(roleHierarchyDescriptionBuffer.toString());
        return roleHierarchy;
    }

}
