package in.ac.amu.zhcet.data.model.notification;

import in.ac.amu.zhcet.data.model.base.BaseIdEntity;
import in.ac.amu.zhcet.data.model.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
public class NotificationRecipient extends BaseIdEntity {

    @NotNull
    @ManyToOne
    private Notification notification;
    @NotNull
    @OneToOne
    private User recipient;
    private boolean favorite;
    private boolean seen;
    private LocalDateTime readTime;

}
