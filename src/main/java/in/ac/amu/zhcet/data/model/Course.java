package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.model.base.entity.BaseEntity;
import in.ac.amu.zhcet.data.model.base.entity.BaseIdEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Course extends BaseEntity {

    @Id
    private String code;
    private String title;
    private boolean active;

    @ManyToOne
    private Department department;
}
