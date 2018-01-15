package amu.zhcet.core.dean.datatables.student;

import amu.zhcet.core.dean.datatables.UserEditModel;
import amu.zhcet.data.user.student.HallCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = false)
class StudentEditModel extends UserEditModel {
    @NotBlank
    @Size(max = 255)
    private String facultyNumber;
    @Enumerated(EnumType.STRING)
    private HallCode hallCode;
    private String section;
    private Character status;
}
