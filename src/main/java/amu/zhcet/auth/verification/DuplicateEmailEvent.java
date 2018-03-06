package amu.zhcet.auth.verification;

import amu.zhcet.data.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DuplicateEmailEvent {
    private final User user;
    private final User duplicateUser;
    private final String email;
}
