package amu.zhcet.common.actuator;

import amu.zhcet.auth.AuthService;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Endpoint(id = "logged-in")
public class LoggedInEndoint {

    private final ModelMapper modelMapper;
    private final AuthService authService;

    public LoggedInEndoint(ModelMapper modelMapper, AuthService authService) {
        this.modelMapper = modelMapper;
        this.authService = authService;
    }

    @Data
    static final class LoggedUser {
        private String username;
        private String name;
        private String avatar;
        private String email;
        private String type;
        private String departmentName;
        private boolean emailVerified;
        private boolean passwordChanged;
        private boolean accountNonExpired;
        private boolean accountNonLocked;
        private boolean credentialsNonExpired;
        private boolean enabled;
        private Set<GrantedAuthority> authorities;
    }

    @ReadOperation
    public List<LoggedUser> getLoggedInUsers() {
        return authService.getUsersFromSessionRegistry()
                .stream()
                .map(customUser -> modelMapper.map(customUser, LoggedUser.class))
                .collect(Collectors.toList());
    }
}