package amu.zhcet.data.user.fcm;

import amu.zhcet.common.model.BaseIdEntity;
import amu.zhcet.data.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "user")
@ToString(exclude = "user")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "fcmToken"}))
public class UserFcmToken extends BaseIdEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable=false, updatable=false)
    @JsonIgnore
    private User user;

    @NotNull
    @Column(name = "user_id")
    private String userId;

    @NotNull
    private String fcmToken;
    private boolean disabled = false;
    private String reason;

    public UserFcmToken(@NotNull String userId, @NotNull String fcmToken) {
        this.userId = userId;
        this.fcmToken = fcmToken;
    }

}
