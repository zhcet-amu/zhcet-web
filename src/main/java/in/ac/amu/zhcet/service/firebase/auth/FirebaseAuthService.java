package in.ac.amu.zhcet.service.firebase.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import in.ac.amu.zhcet.data.model.user.UserAuth;
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
    }

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

    private void setUserAuthentication(UserAuth userAuth) {
        SecurityContextHolder
                .getContext()
                .setAuthentication(userDetailService.authenticationFromUser(userAuth));

        log.info("Logged in user using Social Login: {}", userAuth.getUserId());
    }

    private UserAuth fromFirebaseToken(FirebaseToken token) {
        if (token == null || token.getUid() == null)
            return null;

        UserAuth userAuth = userService.findById(token.getUid());
        if (userAuth != null) {
            return userAuth;
        }

        if (token.getEmail() == null)
            return null;

        if (!token.isEmailVerified())
            log.warn("Unverified Email Login {}", token.getEmail());

        return userService.getUserByEmail(token.getEmail());
    }

    private boolean authenticate(FirebaseToken token) {
        UserAuth userAuth = fromFirebaseToken(token);

        if (userAuth == null) {
            log.warn("Firebase Social Login Failed {}", token.getUid());
            return false;
        }

        setUserAuthentication(userAuth);
        firebaseUserService.mergeFirebaseDetails(userAuth, token);
        return true;
    }

    private FirebaseToken getToken(String token) throws ExecutionException, InterruptedException {
        return FirebaseAuth.getInstance().verifyIdTokenAsync(token).get();
    }

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

    public void linkData(String token) {
        try {
            FirebaseToken decodedToken = getToken(token);
            log.info(decodedToken.getClaims().toString());
            UserAuth user = userService.getLoggedInUser();
            firebaseUserService.mergeFirebaseDetails(user, decodedToken);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error linking data", e);
        }
    }
}
