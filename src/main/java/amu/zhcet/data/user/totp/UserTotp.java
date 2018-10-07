package amu.zhcet.data.user.totp;

import amu.zhcet.common.model.BaseEntity;
import amu.zhcet.data.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "user")
@ToString(exclude = "user")
public class UserTotp extends BaseEntity {
    @Id
    private String userId;

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    @JsonIgnore
    private User user;

    private boolean using2fa;
    private String totpSecret;
}
