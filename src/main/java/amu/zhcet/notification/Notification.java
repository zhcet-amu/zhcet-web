package amu.zhcet.notification;

import amu.zhcet.common.model.BaseIdEntity;
import amu.zhcet.data.user.User;
import amu.zhcet.email.LinkMessage;
import amu.zhcet.notification.recipient.NotificationRecipient;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

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
    private transient boolean stopEmailPropagation;
    private transient boolean stopFirebasePropagation;
    private transient Function<Notification, LinkMessage> linkMessageConverter;

    @NotAudited
    @OneToMany(mappedBy = "notification")
    private List<NotificationRecipient> notificationRecipients;

}
