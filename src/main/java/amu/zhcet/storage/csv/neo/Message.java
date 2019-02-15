package amu.zhcet.storage.csv.neo;

import lombok.Data;

@Data
public class Message {

    private final Type type;
    private final String message;

    public static Message error(String message) {
        return new Message(Type.ERROR, message);
    }

    public static Message warning(String message) {
        return new Message(Type.WARNING, message);
    }

    public static Message success(String message) {
        return new Message(Type.SUCCESS, message);
    }

}
