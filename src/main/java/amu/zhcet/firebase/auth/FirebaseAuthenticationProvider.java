package amu.zhcet.firebase.auth;

import amu.zhcet.auth.UserAuth;
import amu.zhcet.firebase.FirebaseService;
import amu.zhcet.firebase.auth.link.FirebaseAccountMergeService;
import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class FirebaseAuthenticationProvider implements AuthenticationProvider, MessageSourceAware {

    private final FirebaseService firebaseService;
    private final FirebaseAccountMergeService firebaseAccountMergeService;
    private final UserDetailsService userDetailsService;

    private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Autowired
    public FirebaseAuthenticationProvider(FirebaseService firebaseService, FirebaseAccountMergeService firebaseAccountMergeService, UserDetailsService userDetailsService) {
        this.firebaseService = firebaseService;
        this.firebaseAccountMergeService = firebaseAccountMergeService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!firebaseService.canProceed())
            return null; // Firebase is disabled, so we cannot proceed

        String token = authentication.getCredentials().toString();
        if (Strings.isNullOrEmpty(token))
            return null; // Cannot parse empty token

        try {
            FirebaseToken decodedToken = FirebaseService.getToken(token);
            log.debug("User Claims: {}", decodedToken.getClaims());

            UserDetails user = retrieveUser(decodedToken);
            if (user == null)
                throwBadCredentialsException();

            userDetailsChecker.check(user);

            if (user instanceof UserAuth) {
                firebaseAccountMergeService.mergeFirebaseDetails((UserAuth) user, decodedToken);
            } else {
                log.warn("User {} is not of UserAuth Type", user);
            }

            return createSuccessAuthentication(user, authentication);
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Unable to decode Firebase token");
            throwBadCredentialsException();
        } catch (UsernameNotFoundException une) {
            throwBadCredentialsException();
        }

        return null;
    }

    private UserDetails retrieveUser(FirebaseToken decodedToken) {
        String username = decodedToken.getUid();
        if (Strings.isNullOrEmpty(username))
            return null;

        UserDetails user = userDetailsService.loadUserByUsername(username);
        if (user != null)
            return user;

        if (Strings.isNullOrEmpty(decodedToken.getEmail()))
            return null;

        if (!decodedToken.isEmailVerified())
            log.warn("Unverified Email Login {}", decodedToken.getEmail());

        return userDetailsService.loadUserByUsername(decodedToken.getEmail());
    }

    private Authentication createSuccessAuthentication(UserDetails user, Authentication authentication) {
        // Ensure we return the original getDetails(), so that future
        // authentication events after cache expiry contain the details
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
                user, user.getPassword(),
                authoritiesMapper.mapAuthorities(user.getAuthorities()));
        result.setDetails(authentication.getDetails());

        return result;
    }

    private void throwBadCredentialsException() {
        throw new BadCredentialsException(messages.getMessage(
                "AbstractUserDetailsAuthenticationProvider.badCredentials",
                "Bad credentials"));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return FirebaseAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Autowired(required = false)
    public void setUserDetailsChecker(UserDetailsChecker userDetailsChecker) {
        this.userDetailsChecker = userDetailsChecker;
    }

    @Autowired(required = false)
    public void setAuthoritiesMapper(GrantedAuthoritiesMapper authoritiesMapper) {
        this.authoritiesMapper = authoritiesMapper;
    }

    @Override
    @Autowired(required = false)
    public void setMessageSource(@NonNull MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }
}
