package in.ac.amu.zhcet.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User extends BaseEntity {

    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private String userId;

    private String password;
    private String[] roles;

    private String name;
    private String avatarUrl;
    private String addressLine1;
    private String addressLine2;

    @ElementCollection
    private List<String> phoneNumbers = new ArrayList<>();

    protected User() {
        super();
    }

    public User(String userId, String password, String name, String[] roles) {
        this();
        setUserId(userId);
        setPassword(password);
        setName(name);
        setRoles(roles);
    }

    public User(String userId, String password, String name, String[] roles, String avatarUrl, String addressLine1, String addressLine2, List<String> phoneNumbers) {
        this(userId, password, name, roles);
        setAvatarUrl(avatarUrl);
        setAddressLine1(addressLine1);
        setAddressLine2(addressLine2);
        setPhoneNumbers(phoneNumbers);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = PASSWORD_ENCODER.encode(password);
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
}
