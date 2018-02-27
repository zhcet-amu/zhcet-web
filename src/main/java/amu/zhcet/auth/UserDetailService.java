package amu.zhcet.auth;

import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserService;
import amu.zhcet.security.permission.PermissionManager;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;

@Slf4j
@Service
@Transactional
public class UserDetailService implements UserDetailsService {

    private final UserService userService;
    private final PermissionManager permissionManager;

    @Autowired
    public UserDetailService(UserService userService, PermissionManager permissionManager) {
        this.userService = userService;
        this.permissionManager = permissionManager;
    }

    /**
     * Gets the allowed roles for a user
     * @param user User for which the roles are to be found
     * @return Collection of Granted Authorities of the passed in user
     */
    private Collection<GrantedAuthority> getAuthorities(User user) {
        return permissionManager.authorities(user.getRoles());
    }

    /**
     * Get the user from a form of identification.
     * Tries to find user by the ID and then by email
     * @param identification String of identification by which the user is to be found
     * @return User if found by identification
     * @throws UsernameNotFoundException if user is not found
     */
    private User getUser(String identification) throws UsernameNotFoundException  {
        return userService.findById(identification)
                .orElseGet(() -> userService.getUserByEmail(identification)
                        .orElseThrow(() -> new UsernameNotFoundException(identification)));
    }

    /**
     * Tries to find user using {@link #getUser(String)} and transforms into appropriate UserDetails
     * Also finds related information about department and block status of the user
     * @param username String Identification by which the user is to be found
     * @return UserDetails of the user
     * @throws UsernameNotFoundException if user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUser(username);

        // Since we are using lazy properties, we have to initialize Department
        // So that the proxy object is replaced by its actual implementation
        Hibernate.initialize(user.getDepartment());

        Collection<GrantedAuthority> authorities = getAuthorities(user);
        return new UserAuth(user, authorities);
    }

    /**
     * Update current authentication by cloning it with new roles
     * Also saves the user with new roles
     *
     * Used when there is a need to dynamically update a user's roles
     * @param authentication {@link Authentication} to be cloned
     * @param user {@link User} containing roles
     */
    void cloneWithRoles(Authentication authentication, User user) {
        Collection<GrantedAuthority> authorities = getAuthorities(user);

        Authentication clone = new UsernamePasswordAuthenticationToken(
                authentication.getPrincipal(),
                authentication.getCredentials(),
                authorities);

        userService.save(user);
        SecurityContextHolder.getContext().setAuthentication(clone);
    }

    /**
     * Update current authentication by cloning it with password
     * Also saves the user with new password
     *
     * Used when there is a need to dynamically update a user's password like
     * when resetting it or changing it. This is the only method in the project
     * which should be used to change the password of the user
     * @param user {@link User} with new password
     */
    void cloneWithPassword(User user) {
        user.setPasswordChanged(true);
        userService.save(user);
    }

    /**
     * Grants a privilege to user and sets authentication
     * @param user User to be granted a privilege
     * @param privilege String privilege to be granted
     */
    void grantPrivilege(User user, String privilege) {
        Authentication auth = new PreAuthenticatedAuthenticationToken(user, null, Collections.singletonList(new SimpleGrantedAuthority(privilege)));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /**
     * Logs out currently authenticated user
     */
    static void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

}