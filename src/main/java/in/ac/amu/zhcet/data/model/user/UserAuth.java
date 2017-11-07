package in.ac.amu.zhcet.data.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.base.BaseEntity;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Audited
@Builder
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude="details")
@ToString(exclude = "password")
public class UserAuth extends BaseEntity {

    @Id
    @NotBlank
    private String userId;

    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    private Department department;

    private boolean enabled = true;
    private boolean passwordChanged;
    private boolean emailVerified;
    private boolean emailUnsubscribed;

    @NotNull
    @JsonIgnore
    @Size(min = 4, max = 100)
    private String password;
    private String roles;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Type type;

    @Valid
    @NotNull
    @PrimaryKeyJoinColumn
    @OneToOne(cascade = CascadeType.ALL)
    private UserDetail details = new UserDetail();

    public void setUserId(String id) {
        this.userId = id;
        details.setUserId(id);
    }

    public void setRoles(Set<String> roles) {
        this.roles = String.join(",", roles);
    }

    public List<String> getRoles() {
        if (roles != null)
            return Arrays.asList(roles.split(","));

        return null;
    }

    @PrePersist
    public void prePersist() {
        if (details.getUserId() == null)
            details.setUserId(userId);
    }
}
