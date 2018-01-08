package amu.zhcet.common.actuator;

import amu.zhcet.data.ViewService;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LoggedInUsers implements Endpoint<List<LoggedInUsers.LoggedUser>> {

    private final ModelMapper modelMapper;
    private final ViewService viewService;

    @Autowired
    public LoggedInUsers(ModelMapper modelMapper, ViewService viewService) {
        this.modelMapper = modelMapper;
        this.viewService = viewService;
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

    public String getId() {
        return "loggedin";
    }

    public boolean isEnabled() {
        return true;
    }

    public boolean isSensitive() {
        return true;
    }

    public List<LoggedUser> invoke() {
        return viewService.getUsersFromSessionRegistry()
                .stream()
                .map(customUser -> modelMapper.map(customUser, LoggedUser.class))
                .collect(Collectors.toList());
    }
}