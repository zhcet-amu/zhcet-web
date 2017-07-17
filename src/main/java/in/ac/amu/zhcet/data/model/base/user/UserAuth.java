package in.ac.amu.zhcet.data.model.base.user;

import in.ac.amu.zhcet.data.model.base.entity.BaseEntity;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@DynamicUpdate
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "password")
public class UserAuth extends BaseEntity {

    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Id
    private String userId;

    @NotNull
    private String password;
    @NotNull
    private String[] roles;

    @NotNull
    private String name;
    @NotNull
    private String type;

    public UserAuth(String userId, String password, String name, String type, String[] roles) {
        setUserId(userId);
        setPassword(password);
        setName(name);
        setType(type);
        setRoles(roles);
    }
}
