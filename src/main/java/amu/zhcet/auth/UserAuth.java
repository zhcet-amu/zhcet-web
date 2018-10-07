package amu.zhcet.auth;

import amu.zhcet.data.department.Department;
import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserType;
import amu.zhcet.data.user.totp.UserTotp;
import amu.zhcet.security.CryptoUtils;
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
    private final boolean using2fa;
    private String avatar;
    @Setter(value = AccessLevel.NONE)
    private transient String totpSecret;

    UserAuth(User user, boolean locked, Collection<? extends GrantedAuthority> authorities) {
        super(user.getUserId(), user.getPassword(), user.isEnabled(), true, true, !locked, authorities);
        this.name = user.getName();
        this.email = user.getEmail();
        this.type = user.getType();
        this.department = user.getDepartment();
        this.emailVerified = user.isEmailVerified();
        this.passwordChanged = user.isPasswordChanged();

        UserTotp userTotp = user.getTotpDetails();

        if (userTotp != null) {
            this.using2fa = userTotp.isUsing2fa();
            this.totpSecret = userTotp.getTotpSecret();
        } else {
            this.using2fa = false;
        }


        this.avatar = user.getDetails().getAvatarUrl();
    }

    public String getTotpSecret() {
        String secret = CryptoUtils.decrypt(totpSecret, getUsername());
        // We clear the secret after one read
        this.totpSecret = null;
        return secret;
    }

}
