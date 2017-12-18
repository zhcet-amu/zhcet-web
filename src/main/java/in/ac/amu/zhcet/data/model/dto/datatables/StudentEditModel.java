package in.ac.amu.zhcet.data.model.dto.datatables;

import in.ac.amu.zhcet.data.type.HallCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = false)
public class StudentEditModel extends UserEditModel {
    @NotBlank
    @Size(max = 255)
    private String facultyNumber;
    @Enumerated(EnumType.STRING)
    private HallCode hallCode;
    private String section;
    private Character status;
}
