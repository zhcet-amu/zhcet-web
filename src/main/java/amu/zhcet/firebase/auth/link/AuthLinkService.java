package amu.zhcet.firebase.auth.link;

import amu.zhcet.data.user.UserService;
import amu.zhcet.firebase.FirebaseService;
import com.google.firebase.auth.FirebaseToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Slf4j
@Service
class AuthLinkService {

    private final FirebaseService firebaseService;
    private final FirebaseAccountMergeService firebaseAccountMergeService;
    private final UserService userService;

    @Autowired
    public AuthLinkService(FirebaseService firebaseService, FirebaseAccountMergeService firebaseAccountMergeService, UserService userService) {
        this.firebaseService = firebaseService;
        this.firebaseAccountMergeService = firebaseAccountMergeService;
        this.userService = userService;
    }

    /**
     * Links authenticated user to one of the Identity providers, like Google
     * Also merges the provider data like email, verification status, and photo into user account
     * NOTE: Only to be called from an authenticated endpoint
     * @param token String: Firebase Authentication Token
     */
    public void linkAccount(String token) {
        if (!firebaseService.canProceed())
            return;

        try {
            FirebaseToken decodedToken = FirebaseService.getToken(token);
            log.info(decodedToken.getClaims().toString());
            userService.getLoggedInUser().ifPresent(user ->
                    firebaseAccountMergeService.mergeFirebaseDetails(user, decodedToken));
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error linking data", e);
        }
    }

}
