package in.ac.amu.zhcet.data.model.base;

import in.ac.amu.zhcet.data.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public abstract class BaseIdEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id = null;
}
