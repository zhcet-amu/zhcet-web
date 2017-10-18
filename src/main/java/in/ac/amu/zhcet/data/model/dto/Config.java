package in.ac.amu.zhcet.data.model.dto;

import in.ac.amu.zhcet.utils.Utils;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class Config {
    @Size(max = 255)
    private String siteUrl;
    private int threshold;
    @NotNull
    private char term;
    private int year;
    private boolean automatic;
    private final String defaultSession = Utils.getDefaultSessionName();
}
