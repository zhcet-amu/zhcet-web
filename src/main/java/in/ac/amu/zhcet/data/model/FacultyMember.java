package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.model.base.BaseEntity;
import in.ac.amu.zhcet.data.model.user.UserAuth;
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

    private boolean working = true;
    private String designation;

    public FacultyMember(UserAuth user) {
        this.user = user;
        this.facultyId = user.getUserId();
    }

    @PrePersist
    public void prePersist() {
        if (facultyId == null)
            facultyId = user.getUserId();
    }
}
