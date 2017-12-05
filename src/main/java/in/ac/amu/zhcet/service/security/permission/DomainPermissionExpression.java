package in.ac.amu.zhcet.service.security.permission;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

@Slf4j
public class DomainPermissionExpression extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private final PermissionManager permissionManager;

    private Object filterObject;
    private Object returnObject;
    private Object target;

    public DomainPermissionExpression(Authentication authentication, PermissionManager permissionManager) {
        super(authentication);
        this.permissionManager = permissionManager;
    }

    public boolean isNotNull(Object filterObject) {
        return filterObject != null;
    }

    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getFilterObject() {
        return filterObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public Object getReturnObject() {
        return returnObject;
    }

    void setThis(Object target) {
        this.target = target;
    }

    @Override
    public Object getThis() {
        return target;
    }
}
