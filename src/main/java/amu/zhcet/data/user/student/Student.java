package amu.zhcet.data.user.student;

import amu.zhcet.common.model.BaseEntity;
import amu.zhcet.data.user.User;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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

    @Enumerated(EnumType.STRING)
    private HallCode hallCode;
    private String section;
    private Integer registrationYear = getYear();
    private Character status = 'A';

    @NotNull
    @PrimaryKeyJoinColumn
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
