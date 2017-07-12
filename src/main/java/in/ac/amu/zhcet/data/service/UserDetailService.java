package in.ac.amu.zhcet.data.service;

import in.ac.amu.zhcet.data.model.base.user.UserPrincipal;
import in.ac.amu.zhcet.data.service.user.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserDetailService implements UserDetailsService {

    private final UserPrincipalService userPrincipalService;

    @Autowired
    public UserDetailService(UserPrincipalService userPrincipalService) {
        this.userPrincipalService = userPrincipalService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserPrincipal user = userPrincipalService.findById(username);
        if (user == null)
            throw new UsernameNotFoundException(username);

        return new CustomUser(user.getUsername(), user.getPassword(), getAuthorities(user.getRoles()))
                .name(user.getName())
                .avatar(user.getAvatar())
                .type(user.getType())
                .department(user.getDepartment());
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String... roles) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        for (String role: roles)
            grantedAuthorities.add(new SimpleGrantedAuthority(role));

        return grantedAuthorities;
    }
}