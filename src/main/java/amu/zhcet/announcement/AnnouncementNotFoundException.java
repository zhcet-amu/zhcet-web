package amu.zhcet.announcement;

import amu.zhcet.data.ItemNotFoundException;

import javax.annotation.Nullable;

public class AnnouncementNotFoundException extends ItemNotFoundException {
    public AnnouncementNotFoundException() {
        super("Announcement");
    }

    public AnnouncementNotFoundException(@Nullable String id) {
        super("Announcement", id);
    }
}
