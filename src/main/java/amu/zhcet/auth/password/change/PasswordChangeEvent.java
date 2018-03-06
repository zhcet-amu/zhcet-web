package amu.zhcet.auth.password.change;

import amu.zhcet.data.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordChangeEvent {
    private User user;
}
