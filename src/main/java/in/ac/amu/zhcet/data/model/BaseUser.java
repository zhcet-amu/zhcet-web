package in.ac.amu.zhcet.data.model;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.List;

@Entity
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

    public BaseUser() {
        super();
    }

    public BaseUser(String userId, String password, String name, String[] roles) {
        this();
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
        this.password = password;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "BaseUser{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", addressLine1='" + addressLine1 + '\'' +
                ", addressLine2='" + addressLine2 + '\'' +
                ", isActive=" + isActive +
                ", phoneNumbers=" + phoneNumbers +
                '}';
    }
}
