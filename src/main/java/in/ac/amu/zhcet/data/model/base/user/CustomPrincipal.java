package in.ac.amu.zhcet.data.model.base.user;

import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.base.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public class CustomPrincipal extends BaseEntity implements UserPrincipal {

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    @NotNull
    @Valid
    private UserAuth user;

    @Embedded
    @NotNull
    @Valid
    private UserDetails userDetails = new UserDetails();

    public CustomPrincipal(UserAuth userAuth) {
        this.user = userAuth;
    }

    @Override
    public String getUsername() {
        return user.getUserId();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String[] getRoles() {
        return user.getRoles();
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public String getAvatar() {
        return userDetails.getAvatarUrl();
    }

    @Override
    public String getType() {
        return user.getType();
    }

    @Override
    public Department getDepartment() {
        return userDetails.getDepartment();
    }
}
