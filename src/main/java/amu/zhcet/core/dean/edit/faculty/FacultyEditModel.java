package amu.zhcet.core.dean.edit.faculty;

import amu.zhcet.core.dean.edit.UserEditModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = false)
class FacultyEditModel extends UserEditModel {
    private boolean working;
    @Size(max = 255)
    private String designation;
}
