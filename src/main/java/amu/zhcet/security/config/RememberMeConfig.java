package amu.zhcet.security.config;

import amu.zhcet.auth.UserDetailService;
import amu.zhcet.auth.login.persistent.PersistentTokenService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class RememberMeConfig {

    private final PersistentTokenService persistentTokenService;
    private final UserDetailService userDetailService;

    public RememberMeConfig(PersistentTokenService persistentTokenService, UserDetailService userDetailService) {
        this.persistentTokenService = persistentTokenService;
        this.userDetailService = userDetailService;
    }

    public void configure(HttpSecurity http) throws Exception {
        http.rememberMe()
                .rememberMeCookieName("ZHCET_REMEMBER_ME")
                .tokenValiditySeconds(24 * 60 * 60 * 7) // 1 week
                .tokenRepository(persistentTokenService)
                .userDetailsService(userDetailService);
    }

}
