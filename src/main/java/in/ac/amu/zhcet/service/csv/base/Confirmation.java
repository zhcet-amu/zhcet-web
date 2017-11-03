package in.ac.amu.zhcet.service.csv.base;

import in.ac.amu.zhcet.data.model.base.Meta;
import lombok.Data;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Set;

@Data
public class Confirmation<T extends Meta> {
    @NonNull
    private Set<String> errors = new HashSet<>();
    @NonNull
    private Set<T> data = new HashSet<>();
}