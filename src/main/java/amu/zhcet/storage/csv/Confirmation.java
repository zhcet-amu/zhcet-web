package amu.zhcet.storage.csv;

import amu.zhcet.common.model.Meta;
import lombok.Data;
import lombok.NonNull;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class Confirmation<T extends Meta> {
    @NonNull
    private final Set<String> errors = new HashSet<>();
    @NonNull
    private final Set<T> data = new LinkedHashSet<>();
}