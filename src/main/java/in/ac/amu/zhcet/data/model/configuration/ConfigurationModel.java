package in.ac.amu.zhcet.data.model.configuration;

import in.ac.amu.zhcet.utils.Utils;
import lombok.Data;

/**
 * This model will be saved in database as JSON
 */

@Data
public class ConfigurationModel {
    public static final int VERSION = 2;

    private int attendanceThreshold = 75;
    private String session = Utils.getDefaultSessionCode();
    private boolean automatic = true;
    private int version;

    public ConfigurationModel() {
        version = VERSION;
    }
}
