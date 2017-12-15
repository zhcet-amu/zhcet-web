package in.ac.amu.zhcet.service.user;

import in.ac.amu.zhcet.data.model.user.User;
import in.ac.amu.zhcet.service.UserService;
import in.ac.amu.zhcet.service.security.login.LoginAttemptService;
import in.ac.amu.zhcet.service.security.permission.PermissionManager;
import in.ac.amu.zhcet.service.upload.image.ImageService;
import in.ac.amu.zhcet.utils.Utils;
import lombok.extern.slf4j.Slf4j;
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
import java.time.ZonedDateTime;
import java.util.Optional;

@Slf4j
@Service
public class UserDetailService implements UserDetailsService {

    private final UserService userService;
    private final LoginAttemptService loginAttemptService;

    @Autowired // Field injection is required as the value will change on each request
    private HttpServletRequest request;

    @Autowired
    public UserDetailService(UserService userService, LoginAttemptService loginAttemptService) {
        this.userService = userService;
        this.loginAttemptService = loginAttemptService;
    }

    public UserService getUserService() {
        return userService;
    }

    private static UserDetails detailsFromUser(User user, boolean isBlocked) {
        return new CustomUser(user.getUserId(), user.getPassword(), user.isEnabled(), isBlocked,
                PermissionManager.authorities(user.getRoles()))
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
        return detailsFromUser(user, loginAttemptService.isBlocked(ip));
    }

    private static Authentication authenticationFromUserAuth(User user, UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails, user.getPassword(), PermissionManager.authorities(user.getRoles())
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

    public static void updateStaticPrincipal(User user) {
        SecurityContextHolder.getContext().setAuthentication(
                authenticationFromUserAuth(user, detailsFromUser(user, false))
        );
    }

    public void updatePrincipal(User user) {
        // Update the principal for use throughout the app
        SecurityContextHolder.getContext().setAuthentication(authenticationFromUser(user));
    }

    public Optional<User> getLoggedInUser() {
        return userService.getLoggedInUser();
    }

    @Transactional
    public void updateAvatar(User user, ImageService.Avatar avatar) {
        user.getDetails().setAvatarUrl(avatar.getAvatarUrl());
        user.getDetails().setOriginalAvatarUrl(avatar.getOriginalAvatarUrl());
        user.getDetails().setAvatarUpdated(ZonedDateTime.now());
        userService.save(user);
        updatePrincipal(user);
    }
}