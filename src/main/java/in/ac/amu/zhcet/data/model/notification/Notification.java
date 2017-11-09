package in.ac.amu.zhcet.data.model.notification;

import in.ac.amu.zhcet.data.model.base.BaseIdEntity;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Audited
@EqualsAndHashCode(callSuper = false)
public class Notification extends BaseIdEntity {

    @NotBlank
    @OneToOne
    private UserAuth sender;
    private String title;
    @NotBlank
    private String message;
    @NotBlank
    private String recipientChannel;
    @NotBlank
    @Enumerated(EnumType.STRING)
    private ChannelType channelType;
    private boolean scheduled;
    private LocalDateTime sentTime;

    @NotAudited
    @OneToMany(mappedBy = "notification")
    private List<NotificationRecipient> notificationRecipients;

}
