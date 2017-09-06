package in.ac.amu.zhcet.data.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class StudentEditModel {
    @NotBlank
    private String facultyNumber;
    @NotBlank
    private String name;
    private String email;
    @NotBlank
    private String department;
}
