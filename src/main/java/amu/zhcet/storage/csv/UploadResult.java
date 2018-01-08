package amu.zhcet.storage.csv;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
public class UploadResult<T> {
    @NonNull
    private final List<String> errors = new ArrayList<>();
    @NonNull
    private final List<T> uploads = new ArrayList<>();
}