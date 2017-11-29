package in.ac.amu.zhcet.data.model.file;

import in.ac.amu.zhcet.data.model.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PasswordFile extends BaseEntity {

    public static final long EXPIRY_TIME = 3;
    public static final TemporalUnit EXPIRY_DURATION = ChronoUnit.HOURS;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @NotBlank
    private String link;

    @NotBlank
    private LocalDateTime createdTime;

    @NotBlank
    private boolean deleted;

    public PasswordFile(String link) {
        this.link = link;
        setCreatedTime();
    }

    public boolean isExpired() {
        return isDeleted() || (LocalDateTime.now().isAfter(getExpiryTime()));
    }

    public LocalDateTime getExpiryTime() {
        return createdTime.plus(EXPIRY_TIME, EXPIRY_DURATION);
    }

    @PrePersist
    public void setCreatedTime() {
        if (createdTime != null)
            return;

        createdTime = LocalDateTime.now();
    }

}
