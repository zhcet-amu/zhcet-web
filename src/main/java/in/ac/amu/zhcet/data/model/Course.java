package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Course extends BaseEntity {

    @Id
    @NotBlank
    private String code;
    @NotBlank
    private String title;
    private boolean active = true;

    @ManyToOne
    @NotNull
    private Department department;
}
