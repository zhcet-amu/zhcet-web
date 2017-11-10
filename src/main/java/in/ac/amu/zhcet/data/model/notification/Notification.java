package in.ac.amu.zhcet.data.model.notification;

import in.ac.amu.zhcet.data.model.base.BaseIdEntity;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Audited
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = "notificationRecipients")
public class Notification extends BaseIdEntity {

    @OneToOne
    private UserAuth sender;
    @Size(max = 150)
    private String title;
    @NotBlank
    @Size(max = 500)
    private String message;
    @NotBlank
    private String recipientChannel;
    @NotNull
    @Enumerated(EnumType.STRING)
    private ChannelType channelType;
    private boolean scheduled;
    private LocalDateTime sentTime = LocalDateTime.now();
    private boolean automated;

    private transient int seenCount;

    @NotAudited
    @OneToMany(mappedBy = "notification")
    private List<NotificationRecipient> notificationRecipients;

}
