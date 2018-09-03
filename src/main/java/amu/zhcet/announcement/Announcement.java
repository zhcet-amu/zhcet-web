package amu.zhcet.announcement;

import amu.zhcet.common.model.BaseIdEntity;
import amu.zhcet.data.user.User;
import amu.zhcet.email.LinkMessage;
import lombok.*;
import org.hibernate.envers.Audited;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.function.Function;

@Data
@Entity
@Audited
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Announcement extends BaseIdEntity {

    @OneToOne(fetch = FetchType.LAZY)
    private User sender;
    @Size(max = 150)
    private String title;
    @NotBlank
    @Size(max = 500)
    private String message;
    @NotNull
    private boolean scheduled;
    private LocalDateTime sentTime = LocalDateTime.now();
    private boolean automated;

    private transient boolean stopEmailPropagation;
    private transient boolean stopFirebasePropagation;
    private transient String icon = "https://zhcet-backend.firebaseapp.com/static/img/icon.png";
    private transient Function<Announcement, LinkMessage> linkMessageConverter;

}
