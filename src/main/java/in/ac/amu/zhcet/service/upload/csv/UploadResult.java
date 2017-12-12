package in.ac.amu.zhcet.service.upload.csv;

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