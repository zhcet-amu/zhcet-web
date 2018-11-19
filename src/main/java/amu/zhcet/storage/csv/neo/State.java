package amu.zhcet.storage.csv.neo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Data
public class State {
    private final long warningCount;
    private final long errorCount;
    private final String string;

    @JsonCreator
    public State(@JsonProperty("warningCount") long warningCount,
                 @JsonProperty("errorCount") long errorCount,
                 @JsonProperty("string") String string) {
        this.warningCount = warningCount;
        this.errorCount = errorCount;

        this.string = string;
    }

    public boolean isValid() {
        return errorCount <= 0;
    }

    public Type getType() {
        if (errorCount > 0) {
            return Type.ERROR;
        } else {
            if (warningCount > 0) {
                return Type.WARNING;
            } else {
                return Type.SUCCESS;
            }
        }
    }

    public boolean isMatching(State previousStatus) {
        return isValid() && string.equals(previousStatus.getString());
    }

    private static <T> Predicate<Wrapper<T>> getPredicateForMessageType(Type type) {
        return wrapper -> wrapper.getMessage() != null && wrapper.getMessage().getType() == type;
    }

    public static <T> State fromWrappers(List<Wrapper<T>> wrappers) {
        long warningCount = wrappers.stream()
                .filter(getPredicateForMessageType(Type.WARNING))
                .count();

        long errorCount = wrappers.stream()
                .filter(getPredicateForMessageType(Type.ERROR))
                .count();

        String status = wrappers.stream().map(item -> {
            if (item.getMessage() == null) {
                return ".";
            } else {
                switch (item.getMessage().getType()) {
                    case ERROR:
                        return "E";
                    case WARNING:
                        return "W";
                    case SUCCESS:
                        return "S";
                    default:
                        return ".";
                }
            }
        }).collect(Collectors.joining());

        return new State(warningCount, errorCount, status);

    }
}