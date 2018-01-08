package amu.zhcet.core.auth.login.persistent;

import amu.zhcet.common.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PersistentLogin extends BaseEntity {
    @Id
    private String series;
    private String username;
    private String token;
    private Date lastUsed;
}
