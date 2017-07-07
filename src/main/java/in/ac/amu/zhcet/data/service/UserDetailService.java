package in.ac.amu.zhcet.data.service;

import in.ac.amu.zhcet.data.model.base.BaseUser;
import in.ac.amu.zhcet.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        BaseUser user = userRepository.findByUserId(username);
        if (user == null)
            throw new UsernameNotFoundException(username);

        return new User(user.getUserId(), user.getPassword(), getAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String... roles) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        for (String role: roles)
            grantedAuthorities.add(new SimpleGrantedAuthority(role));

        return grantedAuthorities;
    }
}