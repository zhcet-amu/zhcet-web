package amu.zhcet.storage.csv.neo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Wrapper<T> {
    private final T item;
    private Message message;
}
