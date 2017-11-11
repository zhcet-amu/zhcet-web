package in.ac.amu.zhcet.service.notification.email.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class InvalidMessageException extends RuntimeException {

    private String className;
    private final List<String> fields;

    public InvalidMessageException(String className, List<String> fields) {
        super(String.format("%s instance fields are invalid : %s", className, fields));
        this.className = className;
        this.fields = fields;
    }

}
