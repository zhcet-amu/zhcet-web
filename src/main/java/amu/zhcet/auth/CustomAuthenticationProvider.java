package amu.zhcet.auth;

import amu.zhcet.auth.login.LoginAttemptService;
import amu.zhcet.auth.twofactor.TwoFAService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    private final LoginAttemptService loginAttemptService;

    public CustomAuthenticationProvider(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Autowired
    public void setUserDetailService(UserDetailService userDetailService) {
        setUserDetailsService(userDetailService);
    }

    @Autowired
    public void setPasswordEncoderBean(PasswordEncoder passwordEncoder) {
        setPasswordEncoder(passwordEncoder);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userId = (String) authentication.getPrincipal();
        CustomAuthenticationDetails details = (CustomAuthenticationDetails) authentication.getDetails();

        boolean isBlocked = loginAttemptService.isBlocked(userId);

        if (isBlocked) {
            log.debug("User account is locked");

            throw new LockedException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.locked",
                    "User account is locked"));
        }

        Authentication authenticated = super.authenticate(authentication);

        UserAuth userAuth = (UserAuth) authenticated.getPrincipal();

        if (!userAuth.isUsing2fa())
            return authenticated;

        String code = details.getTotpCode();
        String secret = userAuth.getTotpSecret();
        if (secret == null || code == null) {
            throw new BadCredentialsException("OTP was not provided");
        } else if (!TwoFAService.isValidOtp(secret, code)) {
            throw new BadCredentialsException("OTP was incorrect. Please try again");
        }

        return authenticated;
    }

}
