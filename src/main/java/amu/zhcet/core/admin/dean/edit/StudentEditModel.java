package amu.zhcet.core.admin.dean.edit;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
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
