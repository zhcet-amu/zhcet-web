package amu.zhcet.core.dean.edit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = false)
class StudentEditModel extends UserEditModel {
    @NotBlank
    @Size(max = 255)
    private String facultyNumber;
    private String hallCode;
    private String section;
    private Character status;
}
