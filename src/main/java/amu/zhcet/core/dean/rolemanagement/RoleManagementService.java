package amu.zhcet.core.dean.rolemanagement;

import amu.zhcet.core.ViewService;
import amu.zhcet.core.auth.UserDetailService;
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

    private final UserDetailService userDetailService;
    private final ViewService viewService;

    @Autowired
    public RoleManagementService(UserDetailService userDetailService, ViewService viewService) {
        this.userDetailService = userDetailService;
        this.viewService = viewService;
    }

    private Set<String> getOptimalRoles(List<String> roles) {
        if (roles == null)
            roles = new ArrayList<>();

        Set<String> reachableRoles = roles.stream()
                .map(Collections::singletonList)
                .map(viewService::getOnlyReachableRoles)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        return roles.stream()
                .filter(role -> !reachableRoles.contains(role))
                .collect(Collectors.toSet());
    }

    public void saveRoles(User user, List<String> roles) {
        Set<String> optimalRoles = getOptimalRoles(roles);

        if (optimalRoles.isEmpty()) {
            optimalRoles.add(user.getType().equals(UserType.STUDENT) ?
                    Role.STUDENT.toString() : Role.FACULTY.toString());
        }

        user.setRoles(optimalRoles);
        userDetailService.getUserService().save(user);
        userDetailService.getLoggedInUser()
                .filter(member -> user.getUserId().equals(member.getUserId()))
                .ifPresent(userDetailService::updatePrincipal);
    }

    public Map<String, List<String>> getRoleHierarchyMap() {
        Function<List<String>, List<String>> lowerCasing = list ->
                list.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        return Arrays.stream(Role.values())
                .map(Role::toString)
                .collect(Collectors.toMap(String::toLowerCase, role ->
                        lowerCasing.apply(viewService.getOnlyReachableRoles(Collections.singletonList(role)))));
    }

}
