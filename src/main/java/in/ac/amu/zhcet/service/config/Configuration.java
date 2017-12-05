package in.ac.amu.zhcet.service.config;

import in.ac.amu.zhcet.data.model.base.BaseEntity;
import in.ac.amu.zhcet.utils.Utils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Configuration extends BaseEntity {

    @Id
    private Long id;

    @Min(50)
    @Max(100)
    private int attendanceThreshold = 75;
    private String session = Utils.getDefaultSessionCode();
    private String url;
    private boolean automatic = true;
    @Min(3)
    @Max(10)
    private int maxRetries = 5;
    @Min(3)
    @Max(24)
    private int blockDuration = 6; // hours
}
