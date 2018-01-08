package amu.zhcet.common.error;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DuplicateException extends RuntimeException {

    private static final String FORMAT = "%s with %s '%s' already exists";

    private Object item;
    private String type;
    private String identifier;

    public DuplicateException(String type) {
        super("This " + type + " already exists");
    }

    public DuplicateException(String type, Object item) {
        this(type);
        setItem(item);
    }

    public DuplicateException(String type, Long id) {
        this(type, "id", id);
    }

    public DuplicateException(String type, String field, Long id) {
        super(String.format(FORMAT, type, field, id));
    }

    public DuplicateException(String type, Long id, Object item) {
        this(type, id);
        setItem(item);
    }

    public DuplicateException(String type, String field, Long id, Object item) {
        this(type, field, id);
        setItem(item);
    }

    public DuplicateException(String type, String id) {
        this(type, "id", id);
    }

    public DuplicateException(String type, String field, String id) {
        super(String.format(FORMAT, type, field, id));
    }

    public DuplicateException(String type, String id, Object item) {
        this(type, id);
        setItem(item);
    }

    public DuplicateException(String type, String field, String id, Object item) {
        this(type, field, id);
        setItem(item);
    }
}
