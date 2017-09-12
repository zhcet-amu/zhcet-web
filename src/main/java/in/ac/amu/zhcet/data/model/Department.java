package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.model.base.BaseIdEntity;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Department extends BaseIdEntity {
    @NotBlank
    @Column(unique = true)
    private String name;
}
