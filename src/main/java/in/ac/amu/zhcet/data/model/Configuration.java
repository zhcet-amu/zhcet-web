package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.model.base.BaseIdEntity;
import in.ac.amu.zhcet.utils.Utils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Configuration extends BaseIdEntity {
    @Min(50)
    @Max(100)
    private int attendanceThreshold = 75;
    private String session = Utils.getDefaultSessionCode();
    private String url;
    private boolean automatic = true;
    @Min(3)
    @Max(10)
    private int maxRetries= 5;
    @Min(3)
    @Max(24)
    private int blockDuration = 6;
}
