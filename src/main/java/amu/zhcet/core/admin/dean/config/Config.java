package amu.zhcet.core.admin.dean.config;

import amu.zhcet.common.utils.Utils;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
class Config {

    private final String defaultSession = Utils.getDefaultSessionName();

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
    @Min(3)
    @Max(10)
    private int maxRetries;
    @Min(5)
    @Max(120)
    private int blockDuration;
}
