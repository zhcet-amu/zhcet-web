package in.ac.amu.zhcet.data.model.token;

import in.ac.amu.zhcet.data.model.base.BaseIdEntity;
import in.ac.amu.zhcet.data.model.user.User;
import in.ac.amu.zhcet.data.model.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class PasswordResetToken extends BaseIdEntity{

    private static final int EXPIRATION = 60 * 3;

    @NotNull
    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @NotNull
    private Date expiry;

    @NotNull
    private boolean used;

    @PrePersist
    public void prePersist(){
        if (expiry != null) return;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 3);
        expiry = calendar.getTime();
    }
}