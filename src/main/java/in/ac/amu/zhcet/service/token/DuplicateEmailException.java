package in.ac.amu.zhcet.service.token;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("'" + email + "' is already registered by another user");
    }

}
