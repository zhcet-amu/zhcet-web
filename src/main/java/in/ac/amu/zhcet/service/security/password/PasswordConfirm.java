package in.ac.amu.zhcet.service.security.password;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class PasswordConfirm {
    @Size(min = 8)
    private String newPassword;
    @Size(min = 8)
    private String confirmPassword;
}