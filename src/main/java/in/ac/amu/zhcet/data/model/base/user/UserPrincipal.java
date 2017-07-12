package in.ac.amu.zhcet.data.model.base.user;

import in.ac.amu.zhcet.data.model.Department;

public interface UserPrincipal {

    String getUsername();

    String getPassword();

    String[] getRoles();

    String getName();

    String getAvatar();

    String getType();

    Department getDepartment();

}
