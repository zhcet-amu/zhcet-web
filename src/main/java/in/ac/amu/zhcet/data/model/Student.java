package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.model.base.BaseEntity;
import in.ac.amu.zhcet.data.model.user.User;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
@Audited
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Student extends BaseEntity {

    @Id
    private String enrolmentNumber;

    @NotBlank
    @Column(unique = true)
    private String facultyNumber;

    @Size(max = 2)
    private String hallCode;
    private String section;
    private Integer registrationYear = getYear();
    private Character status = 'A';

    @NotNull
    @PrimaryKeyJoinColumn
    @OneToOne(cascade = CascadeType.ALL)
    private User user = new User();

    public Student(User user, String facultyNumber) {
        this.user = user;
        setFacultyNumber(facultyNumber);
    }

    private Integer getYear() {
        if (getCreatedAt() == null)
            return null;

        return getCreatedAt().getYear();
    }

    @PrePersist
    public void prePersist() {
        if (enrolmentNumber == null)
            enrolmentNumber = user.getUserId();
    }
}
