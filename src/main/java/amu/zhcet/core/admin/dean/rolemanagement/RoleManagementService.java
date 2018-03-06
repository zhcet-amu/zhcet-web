package amu.zhcet.core.admin.dean.rolemanagement;

import amu.zhcet.auth.AuthManager;
import amu.zhcet.auth.AuthService;
import amu.zhcet.data.user.Role;
import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
class RoleManagementService {

    private final AuthService authService;
    private final AuthManager authManager;

    @Autowired
    public RoleManagementService(AuthService authService, AuthManager authManager) {
        this.authService = authService;
        this.authManager = authManager;
    }

    public void saveRoles(User user, List<String> roles) {
        Set<String> optimalRoles = authService.getOptimalRoles(roles);

        if (optimalRoles.isEmpty()) {
            optimalRoles.add(user.getType().equals(UserType.STUDENT) ?
                    Role.STUDENT.toString() : Role.FACULTY.toString());
        }

        user.setRoles(optimalRoles);
        authManager.updateRoles(user);
    }

    public Map<String, List<String>> getRoleHierarchyMap() {
        Function<List<String>, List<String>> lowerCasing = list ->
                list.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        return Arrays.stream(Role.values())
                .map(Role::toString)
                .collect(Collectors.toMap(String::toLowerCase, role ->
                        lowerCasing.apply(authService.getOnlyReachableRoles(Collections.singletonList(role)))));
    }

}
