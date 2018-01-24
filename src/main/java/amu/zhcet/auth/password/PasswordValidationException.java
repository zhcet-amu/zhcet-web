package amu.zhcet.auth.password;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PasswordValidationException extends IllegalStateException { ;

    public PasswordValidationException(String error) {
        super(error);
    }

}
