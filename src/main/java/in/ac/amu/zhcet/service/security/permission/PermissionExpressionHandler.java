package in.ac.amu.zhcet.service.security.permission;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class PermissionExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    private final PermissionManager permissionManager;

    private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    public PermissionExpressionHandler(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
        DomainPermissionExpression root = new DomainPermissionExpression(authentication, permissionManager);
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(this.trustResolver);
        root.setRoleHierarchy(getRoleHierarchy());
        return root;
    }
}