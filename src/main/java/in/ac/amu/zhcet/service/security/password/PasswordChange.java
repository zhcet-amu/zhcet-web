package in.ac.amu.zhcet.service.security.password;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
public class PasswordChange extends PasswordConfirm {
    @Size(min = 8)
    private String oldPassword;
}
