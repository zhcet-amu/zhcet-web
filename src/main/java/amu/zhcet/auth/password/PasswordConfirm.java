package amu.zhcet.auth.password;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class PasswordConfirm {
    @NotBlank
    @Size(min = 8, max = 255)
    private String newPassword;
    @NotBlank
    @Size(min = 8, max = 255)
    private String confirmPassword;
}