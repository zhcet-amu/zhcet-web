package amu.zhcet.notification;

import amu.zhcet.data.ItemNotFoundException;

import javax.annotation.Nullable;

public class NotificationNotFoundException extends ItemNotFoundException {
    public NotificationNotFoundException() {
        super("Notification");
    }

    public NotificationNotFoundException(@Nullable String id) {
        super("Notification", id);
    }
}
