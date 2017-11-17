package in.ac.amu.zhcet.service.firebase.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import in.ac.amu.zhcet.data.model.user.User;
import in.ac.amu.zhcet.service.UserService;
import in.ac.amu.zhcet.service.firebase.FirebaseService;
import in.ac.amu.zhcet.service.user.Auditor;
import in.ac.amu.zhcet.service.user.CustomUser;
import in.ac.amu.zhcet.service.user.UserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class FirebaseAuthService {

    private final UserService userService;
    private final UserDetailService userDetailService;
    private final FirebaseService firebaseService;
    private final FirebaseUserService firebaseUserService;

    @Autowired
    public FirebaseAuthService(UserService userService, UserDetailService userDetailService, FirebaseService firebaseService, FirebaseUserService firebaseUserService) {
        this.userService = userService;
        this.userDetailService = userDetailService;
        this.firebaseService = firebaseService;
        this.firebaseUserService = firebaseUserService;

        log.info("CONFIG (Firebase): Firebase Auth Running : {}", firebaseService.canProceed());
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
            FirebaseToken decodedToken = getToken(token);
            log.info("User Claims: {}", decodedToken.getClaims());

            return authenticate(decodedToken) ? "/" : errorUrl;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Unable to decode Firebase token", e);
        }

        return errorUrl;
    }

    /**
     * Generates custom firebase token for authenticated user
     * Note: Only to be called from an authenticated endpoint
     * @return UserToken
     */
    public UserToken generateToken() {
        if (!firebaseService.canProceed())
            return null;

        try {
            CustomUser user = Auditor.getLoggedInUser();
            if (user == null) return FirebaseUserService.getUnauthenticated();
            Map<String, Object> claims = new HashMap<>();
            claims.put("type", user.getType().toString());
            claims.put("department", user.getDepartment().getName());
            String token = FirebaseAuth.getInstance().createCustomTokenAsync(user.getUsername(), claims).get();
            return firebaseUserService.fromUser(user, token);
        } catch (InterruptedException | ExecutionException e) {
            return FirebaseUserService.getUnauthenticated();
        }
    }

    /**
     * Links authenticated user to one of the Identity providers, like Google
     * Also merges the provider data like email, verification status, and photo into user account
     * NOTE: Only to be called from an authenticated endpoint
     * @param token String: Firebase Authentication Token
     */
    public void linkData(String token) {
        if (!firebaseService.canProceed())
            return;

        try {
            FirebaseToken decodedToken = getToken(token);
            log.info(decodedToken.getClaims().toString());
            User user = userService.getLoggedInUser();
            firebaseUserService.mergeFirebaseDetails(user, decodedToken);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error linking data", e);
        }
    }

    private void setUserAuthentication(User user) {
        SecurityContextHolder
                .getContext()
                .setAuthentication(userDetailService.authenticationFromUser(user));

        log.info("Logged in user using Social Login: {}", user.getUserId());
    }

    private User fromFirebaseToken(FirebaseToken token) {
        if (token == null || token.getUid() == null)
            return null;

        User user = userService.findById(token.getUid());
        if (user != null) {
            return user;
        }

        if (token.getEmail() == null)
            return null;

        if (!token.isEmailVerified())
            log.warn("Unverified Email Login {}", token.getEmail());

        return userService.getUserByEmail(token.getEmail());
    }

    private boolean authenticate(FirebaseToken token) {
        User user = fromFirebaseToken(token);

        if (user == null) {
            log.warn("Firebase Social Login Failed {}", token.getUid());
            return false;
        }

        setUserAuthentication(user);
        firebaseUserService.mergeFirebaseDetails(user, token);
        return true;
    }

    private FirebaseToken getToken(String token) throws ExecutionException, InterruptedException {
        return FirebaseAuth.getInstance().verifyIdTokenAsync(token).get();
    }
}
