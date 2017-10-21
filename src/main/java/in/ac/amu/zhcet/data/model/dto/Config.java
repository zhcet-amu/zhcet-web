package in.ac.amu.zhcet.data.model.dto;

import in.ac.amu.zhcet.utils.Utils;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class Config {
    @Size(max = 255)
    private String url;
    @Min(50)
    @Max(100)
    private int attendanceThreshold;
    @NotNull
    private char term;
    @Min(2000)
    private int year;
    private boolean automatic;
    private final String defaultSession = Utils.getDefaultSessionName();
}
