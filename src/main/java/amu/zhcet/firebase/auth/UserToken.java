package amu.zhcet.firebase.auth;

import lombok.Data;

@Data
public class UserToken {
    private String token;
    private String username;
    private String name;
    private String avatar;
    private String type;
    private String departmentName;
    private boolean authenticated;
}