package in.ac.amu.zhcet.data.model.base.user;

import in.ac.amu.zhcet.data.model.base.entity.BaseEntity;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Arrays;

@Entity
@DynamicUpdate
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "password")
public class UserAuth extends BaseEntity {

    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Id
    @NotBlank
    private String userId;

    @NotNull
    @Size(min = 4, max = 100)
    private String password;
    @NotBlank
    private String roles;
    @NotBlank
    private String name;
    @NotBlank
    private String type;

    public UserAuth(String userId, String password, String name, String type, String[] roles) {
        setUserId(userId);
        setPassword(password);
        setName(name);
        setType(type);
        setRoles(roles);
    }

    public void setRoles(String[] roles) {
        this.roles = String.join(",", roles);
    }

    public String[] getRoles() {
        if (roles != null)
            return roles.split(",");

        return null;
    }
}
