package amu.zhcet.auth.password;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
public class PasswordChange extends PasswordConfirm {
    @NotBlank
    private String oldPassword;
}
