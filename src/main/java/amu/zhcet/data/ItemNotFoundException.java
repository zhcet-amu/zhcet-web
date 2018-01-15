package amu.zhcet.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import javax.annotation.Nullable;

@Data
@EqualsAndHashCode(callSuper = false)
public class ItemNotFoundException extends RuntimeException {

    private final String type;
    @Nullable
    private String id;

    public ItemNotFoundException(@NonNull String type) {
        super("Item of type " + type + " was not found!");
        this.type = type;
    }

    public ItemNotFoundException(String type, @Nullable String id) {
        super("Item of type " + type + " with ID " + id + " was not found!");
        this.type = type;
        this.id = id;
    }

}
