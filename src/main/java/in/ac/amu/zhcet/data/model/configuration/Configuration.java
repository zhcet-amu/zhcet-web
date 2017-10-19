package in.ac.amu.zhcet.data.model.configuration;

import in.ac.amu.zhcet.data.model.base.BaseIdEntity;
import in.ac.amu.zhcet.utils.Utils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Configuration extends BaseIdEntity {
    private int attendanceThreshold = 75;
    private String session = Utils.getDefaultSessionCode();
    private String url;
    private boolean automatic = true;
}
