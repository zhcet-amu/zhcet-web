package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.model.base.entity.BaseEntity;
import in.ac.amu.zhcet.data.model.base.user.UserAuth;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

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
public class Student extends BaseEntity {
    public static final String TYPE = "STUDENT";

    @Id
    private String enrolmentNumber;

    @NotBlank
    @Column(unique = true)
    private String facultyNumber;

    @Valid
    @NotNull
    @PrimaryKeyJoinColumn
    @OneToOne(cascade = CascadeType.ALL)
    private UserAuth user = new UserAuth();

    public Student(UserAuth user, String facultyNumber) {
        this.user = user;
        setFacultyNumber(facultyNumber);
    }

    @PrePersist
    public void prePersist() {
        if (enrolmentNumber == null)
            enrolmentNumber = user.getUserId();
        else if (user.getUserId() == null)
            user.setUserId(enrolmentNumber);
    }
}
