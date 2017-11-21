package in.ac.amu.zhcet.service.user;

import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.user.UserType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
public class CustomUser extends User {

    private String name;
    private String avatar;
    private String email;
    private UserType type;
    private Department department;
    private boolean emailVerified;
    private boolean passwordChanged;

    CustomUser(String username, String password,boolean enabled, boolean blocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, true, true, !blocked, authorities);
    }

    public CustomUser name(String name) {
        setName(name);
        return this;
    }

    public CustomUser avatar(String avatar) {
        setAvatar(avatar);
        return this;
    }

    public CustomUser email(String email) {
        setEmail(email);
        return this;
    }

    public CustomUser type(UserType type) {
        setType(type);
        return this;
    }

    public CustomUser department(Department department) {
        setDepartment(department);
        return this;
    }

    public CustomUser emailVerified(boolean emailVerified) {
        setEmailVerified(emailVerified);
        return this;
    }

    public CustomUser passwordChanged(boolean passwordChanged) {
        setPasswordChanged(passwordChanged);
        return this;
    }

}
