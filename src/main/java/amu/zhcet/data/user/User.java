package amu.zhcet.data.user;

import amu.zhcet.common.model.BaseEntity;
import amu.zhcet.data.department.Department;
import amu.zhcet.data.user.detail.UserDetail;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@Audited
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude="details")
@ToString(of = {"userId", "name", "email"})
public class User extends BaseEntity {

    @Id
    @NotBlank
    private String userId;

    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    private String name;

    @NotNull
    @ManyToOne
    private Department department;

    @Builder.Default
    private boolean enabled = true;
    private boolean passwordChanged;
    private boolean emailVerified;
    private boolean emailUnsubscribed;
    private boolean using2fa;
    private String totpSecret;

    @NotNull
    @JsonIgnore
    @Size(min = 4, max = 100)
    private String password;
    private String roles;
    private String pendingRoles;
    @NotNull
    @Enumerated(EnumType.STRING)
    private UserType type;

    @Valid
    @NotNull
    @PrimaryKeyJoinColumn
    @Builder.Default
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserDetail details = new UserDetail();

    public void setUserId(String id) {
        this.userId = id;
        details.setUserId(id);
    }

    public boolean isEmailVerified() {
        return emailVerified && email != null;
    }

    public void setRoles(Set<String> roles) {
        if (roles == null) // A user is always a USER
            this.roles = Role.USER.toString();
        else
            this.roles = String.join(",", roles);
    }

    public List<String> getRoles() {
        return separate(roles);
    }

    private static List<String> separate(String commaSeparated) {
        if (commaSeparated == null)
            return null;

        return Arrays.stream(commaSeparated.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public void setPendingRoles(Set<String> roles) {
        if (roles == null)
            this.pendingRoles = null;
        else
            this.pendingRoles = String.join(",", roles);
    }

    public List<String> getPendingRoles() {
        return separate(pendingRoles);
    }

    @PrePersist
    public void prePersist() {
        if (details.getUserId() == null)
            details.setUserId(userId);
    }
}
