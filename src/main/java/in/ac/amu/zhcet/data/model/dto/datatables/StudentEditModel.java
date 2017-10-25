package in.ac.amu.zhcet.data.model.dto.datatables;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = false)
public class StudentEditModel extends UserEditModel {
    @NotBlank
    @Size(max = 255)
    private String facultyNumber;
    @Size(max = 2)
    private String hallCode;
    @Size(max = 255)
    private String section;
    private Character status;
}
