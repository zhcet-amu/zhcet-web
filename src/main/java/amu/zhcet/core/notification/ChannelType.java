package amu.zhcet.core.notification;

import amu.zhcet.common.utils.StringUtils;

public enum ChannelType {
    DEPARTMENT,
    COURSE,
    TAUGHT_COURSE,
    SECTION,
    FACULTY,
    GENDER,
    STUDENT;

    @Override
    public String toString() {
        return StringUtils.capitalizeFirst(name().replace('_', ' '));
    }
}
