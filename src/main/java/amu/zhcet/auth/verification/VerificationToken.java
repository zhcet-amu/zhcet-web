package amu.zhcet.auth.verification;

import amu.zhcet.common.model.BaseIdEntity;
import amu.zhcet.data.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class VerificationToken extends BaseIdEntity {

    private static final int EXPIRATION = 60 * 3;

    @NotNull
    private String email;
    @NotNull
    private String token;
    private boolean used;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @NotNull
    private Date expiry;

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public VerificationToken(User user, String token, String email) {
        this.user = user;
        this.token = token;
        this.email = email;
    }

    @PrePersist
    public void prePersist(){
        if (expiry != null) return;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 3);
        expiry = calendar.getTime();
    }

}
