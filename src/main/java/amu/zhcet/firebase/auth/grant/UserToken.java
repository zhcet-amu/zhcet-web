package amu.zhcet.firebase.auth.grant;

import lombok.Data;

@Data
class UserToken {
    private String token;
    private String username;
    private String name;
    private String avatar;
    private String type;
    private String departmentName;
    private boolean authenticated;
}