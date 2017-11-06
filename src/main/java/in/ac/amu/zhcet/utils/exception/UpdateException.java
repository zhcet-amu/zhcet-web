package in.ac.amu.zhcet.utils.exception;

public class UpdateException extends RuntimeException {

    public UpdateException(String field) {
        super(field + " is not updatable for this item!");
    }

}
