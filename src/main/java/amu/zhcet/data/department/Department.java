package amu.zhcet.data.department;

import amu.zhcet.common.model.BaseEntity;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Entity
@Audited
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(of = {"code", "name"})
public class Department extends BaseEntity implements Serializable {
    @Id
    @NotBlank
    @Size(max = 2)
    private String code;

    @NotBlank
    @Column(unique = true)
    private String name;
}
