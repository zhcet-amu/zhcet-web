package amu.zhcet.common.actuator;

import amu.zhcet.auth.login.LoginAttemptService;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.List;

@Endpoint(id = "blocked")
public class BlockedEndpoint {

    private final LoginAttemptService loginAttemptService;

    public BlockedEndpoint(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @ReadOperation
    public List<LoginAttemptService.BlockedUser> getBlockedUsers() {
        return loginAttemptService.getBlockedUsers();
    }

}
