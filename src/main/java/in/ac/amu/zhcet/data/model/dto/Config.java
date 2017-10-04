package in.ac.amu.zhcet.data.model.dto;

import in.ac.amu.zhcet.utils.Utils;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Config {
    private String siteUrl;
    private int threshold;
    @NotNull
    private char term;
    private int year;
    private boolean automatic;
    private final String defaultSession = Utils.getDefaultSessionName();
}
