package amu.zhcet.core.notification;

import amu.zhcet.common.model.BaseIdEntity;
import amu.zhcet.core.notification.recipient.NotificationRecipient;
import amu.zhcet.data.user.User;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Audited
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = "notificationRecipients")
public class Notification extends BaseIdEntity {

    @OneToOne(fetch = FetchType.LAZY)
    private User sender;
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
