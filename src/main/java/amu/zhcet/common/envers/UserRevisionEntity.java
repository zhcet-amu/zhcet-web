package amu.zhcet.common.envers;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import javax.persistence.Entity;

@Data
@Entity
@RevisionEntity(UserRevisionListener.class)
@EqualsAndHashCode(callSuper = false)
public class UserRevisionEntity extends DefaultRevisionEntity {
    private String username;
}
