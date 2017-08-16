package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.Roles;
import in.ac.amu.zhcet.data.model.base.entity.BaseEntity;
import in.ac.amu.zhcet.data.model.base.user.UserAuth;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FacultyMember extends BaseEntity {
    public static final String TYPE = "FACULTY";

    @Id
    private String facultyId;

    @Valid
    @NotNull
    @PrimaryKeyJoinColumn
    @OneToOne(cascade = CascadeType.ALL)
    private UserAuth user = new UserAuth();

    public FacultyMember(UserAuth user) {
        this.user = user;
        this.facultyId = user.getUserId();
    }

    @PrePersist
    public void prePersist() {
        if (facultyId == null)
            facultyId = user.getUserId();
        else if (user.getUserId() == null)
            user.setUserId(facultyId);
    }
}
