package in.ac.amu.zhcet.data.model.notification;

import in.ac.amu.zhcet.data.model.base.BaseIdEntity;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
public class NotificationRecipient extends BaseIdEntity {

    @NotBlank
    @ManyToOne
    private Notification notification;
    @NotBlank
    @OneToOne
    private UserAuth recipient;
    private boolean favorite;
    private boolean seen;
    private LocalDateTime readTime;

}
