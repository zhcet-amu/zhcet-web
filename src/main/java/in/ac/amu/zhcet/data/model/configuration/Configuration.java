package in.ac.amu.zhcet.data.model.configuration;

import in.ac.amu.zhcet.data.model.base.BaseIdEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Convert;
import javax.persistence.Entity;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Configuration extends BaseIdEntity {
    @Convert(converter = ConfigurationConverter.class)
    private ConfigurationModel config = new ConfigurationModel();
}
