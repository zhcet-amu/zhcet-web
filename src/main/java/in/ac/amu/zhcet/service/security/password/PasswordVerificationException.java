package in.ac.amu.zhcet.service.security.password;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class PasswordVerificationException extends IllegalStateException {

    private final List<String> errors = new ArrayList<>();

    PasswordVerificationException(List<String> errors) {
        super(errors.toString());
        this.errors.addAll(errors);
    }

    PasswordVerificationException(String error) {
        super(error);
        this.errors.add(error);
    }

}
