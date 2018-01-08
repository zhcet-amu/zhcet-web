package amu.zhcet.common.actuator;

import amu.zhcet.core.auth.login.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BlockedIps implements Endpoint<List<LoginAttemptService.BlockedIp>> {

    private final LoginAttemptService loginAttemptService;

    @Autowired
    public BlockedIps(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public String getId() {
        return "blockedips";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isSensitive() {
        return true;
    }

    @Override
    public List<LoginAttemptService.BlockedIp> invoke() {
        return loginAttemptService.getBlockedIps();
    }
}
