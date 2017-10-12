package in.ac.amu.zhcet.service.user.auth;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("'" + email + "' is already registered by another user");
    }

}
