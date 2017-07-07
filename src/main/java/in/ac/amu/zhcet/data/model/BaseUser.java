package in.ac.amu.zhcet.data.model;

import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "password")
public class BaseUser extends BaseEntity {

    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Column(unique = true)
    private String userId;

    private String password;
    private String[] roles;

    private String name;
    private String email;
    private String avatarUrl;
    private String addressLine1;
    private String addressLine2;
    private boolean isActive = true;

    @ManyToOne
    private Department department;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> phoneNumbers;

    public BaseUser(String userId, String password, String name, String[] roles) {
        setUserId(userId);
        setPassword(password);
        setName(name);
        setRoles(roles);
    }

    public BaseUser(String userId, String password, String name, String[] roles, String avatarUrl, String addressLine1, String addressLine2, List<String> phoneNumbers) {
        this(userId, password, name, roles);
        setAvatarUrl(avatarUrl);
        setAddressLine1(addressLine1);
        setAddressLine2(addressLine2);
        setPhoneNumbers(phoneNumbers);
    }
}
