package in.ac.amu.zhcet.service.security.password;

public class TokenValidationException extends IllegalStateException {

    TokenValidationException(String message) {
        super(message);
    }

}
