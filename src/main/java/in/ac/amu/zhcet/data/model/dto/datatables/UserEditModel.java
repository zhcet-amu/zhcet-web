package in.ac.amu.zhcet.data.model.dto.datatables;

import in.ac.amu.zhcet.data.type.Gender;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Size;

@Data
public class UserEditModel {
    @NotBlank
    @Size(max = 255)
    private String userName;
    @Email
    @Size(max = 255)
    private String userEmail;
    @NotBlank
    private String userDepartmentName;
    @Size(max = 255)
    private String userDetailsPhoneNumbers;
    @Enumerated(EnumType.STRING)
    private Gender userDetailsGender;
}
