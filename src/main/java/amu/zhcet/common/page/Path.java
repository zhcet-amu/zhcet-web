package amu.zhcet.common.page;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Path {
    private String link;
    private String title;
    private boolean active;
}
