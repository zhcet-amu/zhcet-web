package amu.zhcet.core.auth.password.change;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
public class PasswordChange extends PasswordConfirm {
    @Size(min = 8)
    private String oldPassword;
}
