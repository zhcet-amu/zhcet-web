package in.ac.amu.zhcet.service.user;

import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.service.UserService;
import in.ac.amu.zhcet.service.upload.image.ImageService;
import in.ac.amu.zhcet.service.security.login.LoginAttemptService;
import in.ac.amu.zhcet.service.security.permission.PermissionManager;
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

    public UserDetails detailsFromUserAuth(UserAuth user) {
        String ip = Utils.getClientIP(request);
        return new CustomUser(user.getUserId(), user.getPassword(), user.isEnabled(), loginAttemptService.isBlocked(ip),
                PermissionManager.authorities(user.getRoles()))
                .name(user.getName())
                .avatar(user.getDetails().getAvatarUrl())
                .type(user.getType())
                .department(user.getDepartment());
    }

    public Authentication authenticationFromUserAuth(UserAuth user) {
        return new UsernamePasswordAuthenticationToken(
                detailsFromUserAuth(user), user.getPassword(), PermissionManager.authorities(user.getRoles())
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAuth user = userService.findById(username);

        if (user == null)
            user = userService.getUserByEmail(username);

        if (user == null)
            throw new UsernameNotFoundException(username);

        return detailsFromUserAuth(user);
    }

    public void updatePrincipal(UserAuth userAuth) {
        // Update the principal for use throughout the app
        SecurityContextHolder.getContext().setAuthentication(authenticationFromUserAuth(userAuth));
    }

    public UserAuth getLoggedInUser() {
        return userService.getLoggedInUser();
    }

    @Transactional
    public void updateAvatar(UserAuth user, ImageService.Avatar avatar) {
        user.getDetails().setAvatarUrl(avatar.getAvatarUrl());
        user.getDetails().setOriginalAvatarUrl(avatar.getOriginalAvatarUrl());
        user.getDetails().setAvatarUpdated(ZonedDateTime.now());
        userService.save(user);
        updatePrincipal(user);
    }
}