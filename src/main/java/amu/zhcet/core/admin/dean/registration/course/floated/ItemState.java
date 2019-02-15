package amu.zhcet.core.admin.dean.registration.course.floated;

import amu.zhcet.storage.csv.neo.State;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ItemState {
    private List<String> items;
    private State state;
}
