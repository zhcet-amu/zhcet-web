package in.ac.amu.zhcet.data.service;

import in.ac.amu.zhcet.data.model.base.user.UserAuth;
import in.ac.amu.zhcet.data.model.base.user.UserDetail;
import in.ac.amu.zhcet.data.service.user.CustomUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class UserDetailService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public UserDetailService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAuth user = userService.findById(username);

        if (user == null)
            throw new UsernameNotFoundException(username);

        return new CustomUser(user.getUserId(), user.getPassword(), getAuthorities(user.getRoles()))
                .name(user.getName())
                .avatar(user.getDetails().getAvatarUrl())
                .type(user.getType())
                .department(user.getDetails().getDepartment());
    }

    private static Collection<? extends GrantedAuthority> getAuthorities(String... roles) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        for (String role: roles)
            grantedAuthorities.add(new SimpleGrantedAuthority(role));

        return grantedAuthorities;
    }

    public void updatePrincipal(UserAuth userAuth) {
        // Update the principal for use throughout the app
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                loadUserByUsername(userAuth.getUserId()), userAuth.getPassword(), UserDetailService.getAuthorities(userAuth.getRoles())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Transactional
    public void updateDetails(UserAuth user, UserDetail userDetail) {
        UserDetail details = user.getDetails();
        details.setAvatarUrl(userDetail.getAvatarUrl());
        details.setDescription(userDetail.getDescription());
        details.setAddressLine1(userDetail.getAddressLine1());
        details.setAddressLine2(userDetail.getAddressLine2());
        details.setCity(userDetail.getCity());
        details.setState(userDetail.getState());

        userService.save(user);

        updatePrincipal(user);
    }
}