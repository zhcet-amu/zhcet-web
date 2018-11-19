package amu.zhcet.storage.csv.neo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Confirmation {
    private final boolean success;
    private final String message;
}
