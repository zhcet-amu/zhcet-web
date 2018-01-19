package amu.zhcet.firebase.auth.verify;

import amu.zhcet.core.auth.UserDetailService;
import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserService;
import amu.zhcet.firebase.FirebaseService;
import amu.zhcet.firebase.auth.link.FirebaseAccountMergeService;
import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class TokenVerifyService {

    private final FirebaseService firebaseService;
    private final FirebaseAccountMergeService firebaseAccountMergeService;
    private final UserService userService;
    private final UserDetailService userDetailService;

    @Autowired
    public TokenVerifyService(FirebaseService firebaseService, FirebaseAccountMergeService firebaseAccountMergeService, UserService userService, UserDetailService userDetailService) {
        this.firebaseService = firebaseService;
        this.firebaseAccountMergeService = firebaseAccountMergeService;
        this.userService = userService;
        this.userDetailService = userDetailService;
    }

    /**
     * Receives firebase token from frontend and verifies if a user is valid
     * If user is valid, authenticates the user and sends back login URL to the frontend
     * If user is invalid, sends back to page indicating error
     * @param token String: Firebase token of the user sent from frontend
     * @return String: URL denoting authenticated or error endpoint
     */
    public String getAction(String token) {
        String errorUrl = "/login?invalid_token";

        if (!firebaseService.canProceed())
            return errorUrl;

        try {
            FirebaseToken decodedToken = FirebaseService.getToken(token);
            log.info("User Claims: {}", decodedToken.getClaims());

            return authenticate(decodedToken) ? "/" : errorUrl;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Unable to decode Firebase token", e);
        }

        return errorUrl;
    }

    private boolean authenticate(FirebaseToken token) {
        Optional<User> userOptional = fromFirebaseToken(token);

        if (!userOptional.isPresent()) {
            log.warn("Firebase Social Login Failed {}", token.getUid());
            return false;
        } else {
            User user = userOptional.get();

            setUserAuthentication(user);
            firebaseAccountMergeService.mergeFirebaseDetails(user, token);
            return true;
        }
    }

    private void setUserAuthentication(User user) {
        SecurityContextHolder
                .getContext()
                .setAuthentication(userDetailService.getRealAuthentication(user));

        log.info("Logged in user using Social Login: {}", user.getUserId());
    }

    private Optional<User> fromFirebaseToken(FirebaseToken token) {
        if (token == null || Strings.isNullOrEmpty(token.getUid()))
            return Optional.empty();

        Optional<User> userOptional = userService.findById(token.getUid());
        if (userOptional.isPresent()) {
            return userOptional;
        }

        if (Strings.isNullOrEmpty(token.getEmail()))
            return Optional.empty();

        if (!token.isEmailVerified())
            log.warn("Unverified Email Login {}", token.getEmail());

        return userService.getUserByEmail(token.getEmail());
    }

}
