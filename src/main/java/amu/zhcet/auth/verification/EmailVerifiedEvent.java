package amu.zhcet.auth.verification;

import amu.zhcet.data.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailVerifiedEvent {
    private final User user;
    private final boolean verified;
}
