package amu.zhcet.common.envers;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
@RevisionEntity(UserRevisionListener.class)
@EqualsAndHashCode(callSuper = false)
public class UserRevisionEntity {
    @Id
    @RevisionNumber
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @RevisionTimestamp
    private long timestamp;
    private String username;
}
