package amu.zhcet.auth;

import amu.zhcet.data.department.Department;
import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Slf4j
@Getter
@Setter(value = AccessLevel.PACKAGE)
public class UserAuth extends org.springframework.security.core.userdetails.User {

    private final String name;
    private final String email;
    private final UserType type;
    private final Department department;
    private final boolean emailVerified;
    private final boolean passwordChanged;
    private String avatar;

    UserAuth(User user, boolean blocked, Collection<? extends GrantedAuthority> authorities) {
        super(user.getUserId(), user.getPassword(), user.isEnabled(), true, true, !blocked, authorities);
        this.name = user.getName();
        this.email = user.getEmail();
        this.type = user.getType();
        this.department = user.getDepartment();
        this.emailVerified = user.isEmailVerified();
        this.passwordChanged = user.isPasswordChanged();
        this.avatar = user.getDetails().getAvatarUrl();
    }

}
