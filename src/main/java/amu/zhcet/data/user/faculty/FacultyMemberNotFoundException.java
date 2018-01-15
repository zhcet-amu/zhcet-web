package amu.zhcet.data.user.faculty;

import amu.zhcet.data.ItemNotFoundException;

import javax.annotation.Nullable;

public class FacultyMemberNotFoundException extends ItemNotFoundException {
    public FacultyMemberNotFoundException() {
        super("Faculty Member");
    }

    public FacultyMemberNotFoundException(@Nullable String id) {
        super("Faculty Member", id);
    }
}
