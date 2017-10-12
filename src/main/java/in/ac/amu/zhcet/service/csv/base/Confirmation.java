package in.ac.amu.zhcet.service.csv.base;

import lombok.Data;
import lombok.NonNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class Confirmation<T, V> {
    @NonNull
    private Set<String> errors = new HashSet<>();
    @NonNull
    private Map<T, V> data = new HashMap<>();
}