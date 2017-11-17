package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.model.base.BaseEntity;
import in.ac.amu.zhcet.data.model.user.User;
import in.ac.amu.zhcet.data.model.user.User;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Builder
@Audited
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FacultyMember extends BaseEntity {

    @Id
    private String facultyId;

    @Valid
    @NotNull
    @PrimaryKeyJoinColumn
    @OneToOne(cascade = CascadeType.ALL)
    private User user = new User();

    private boolean working = true;
    private String designation;

    public FacultyMember(User user) {
        this.user = user;
        this.facultyId = user.getUserId();
    }

    @PrePersist
    public void prePersist() {
        if (facultyId == null)
            facultyId = user.getUserId();
    }
}
