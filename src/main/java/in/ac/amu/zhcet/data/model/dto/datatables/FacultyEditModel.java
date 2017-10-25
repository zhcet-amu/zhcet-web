package in.ac.amu.zhcet.data.model.dto.datatables;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = false)
public class FacultyEditModel extends UserEditModel {
    private boolean working;
    @Size(max = 255)
    private String designation;
}
