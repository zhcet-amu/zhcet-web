package amu.zhcet.core.auth;

import amu.zhcet.common.utils.Utils;
import amu.zhcet.core.auth.login.LoginAttemptService;
import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserService;
import amu.zhcet.security.permission.PermissionManager;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

@Slf4j
@Service
@Transactional
public class UserDetailService implements UserDetailsService {

    private final UserService userService;
    private final PermissionManager permissionManager;
    private final LoginAttemptService loginAttemptService;

    @Autowired // Field injection is required as the value will change on each request
    private HttpServletRequest request;

    @Autowired
    public UserDetailService(UserService userService, PermissionManager permissionManager, LoginAttemptService loginAttemptService) {
        this.userService = userService;
        this.permissionManager = permissionManager;
        this.loginAttemptService = loginAttemptService;
    }

    public UserService getUserService() {
        return userService;
    }

    private UserDetails detailsFromUser(User user, boolean isBlocked) {
        // Since we are using lazy properties, we have to initialize Department
        // So that the proxy object is replaced by its actual implementation
        Hibernate.initialize(user.getDepartment());

        return new CustomUser(user.getUserId(), user.getPassword(), user.isEnabled(), isBlocked,
                permissionManager.authorities(user.getRoles()))
                .name(user.getName())
                .avatar(user.getDetails().getAvatarUrl())
                .email(user.getEmail())
                .type(user.getType())
                .department(user.getDepartment())
                .emailVerified(user.isEmailVerified())
                .passwordChanged(user.isPasswordChanged());
    }

    private UserDetails detailsFromUserAuth(User user) {
        String ip = Utils.getClientIP(request);
        return detailsFromUser(user, loginAttemptService.isBlocked(LoginAttemptService.getKey(ip, user.getUserId())));
    }

    private Authentication authenticationFromUserAuth(User user, UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails, user.getPassword(), permissionManager.authorities(user.getRoles())
        );
    }

    public Authentication authenticationFromUser(User user) {
        return authenticationFromUserAuth(user, detailsFromUserAuth(user));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService
                .findById(username)
                .orElseGet(() -> userService
                        .getUserByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException(username)));

        return detailsFromUserAuth(user);
    }

    public void updatePrincipal(User user) {
        // Update the principal for use throughout the app
        SecurityContextHolder.getContext().setAuthentication(authenticationFromUser(user));
    }

    public void saveAndUpdatePrincipal(User user) {
        userService.save(user);
        updatePrincipal(user);
    }
}