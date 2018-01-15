package amu.zhcet.data.user;

import amu.zhcet.data.ItemNotFoundException;

import javax.annotation.Nullable;

public class UserNotFoundException extends ItemNotFoundException {
    public UserNotFoundException() {
        super("User");
    }

    public UserNotFoundException(@Nullable String id) {
        super("User", id);
    }
}
