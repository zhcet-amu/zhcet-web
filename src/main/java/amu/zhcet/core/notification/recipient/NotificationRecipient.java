package amu.zhcet.core.notification.recipient;

import amu.zhcet.common.model.BaseIdEntity;
import amu.zhcet.core.notification.Notification;
import amu.zhcet.data.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
