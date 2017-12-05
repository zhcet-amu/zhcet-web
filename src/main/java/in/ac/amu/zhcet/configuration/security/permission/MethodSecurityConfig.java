package in.ac.amu.zhcet.configuration.security.permission;

import in.ac.amu.zhcet.service.security.permission.PermissionExpressionHandler;
import in.ac.amu.zhcet.service.security.permission.PermissionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    private final PermissionManager permissionManager;

    @Autowired
    public MethodSecurityConfig(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return new PermissionExpressionHandler(permissionManager);
    }
}