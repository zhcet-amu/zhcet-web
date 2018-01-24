package amu.zhcet.firebase.auth.link;

import amu.zhcet.auth.UserAuth;
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

    @Autowired
    public AuthLinkService(FirebaseService firebaseService, FirebaseAccountMergeService firebaseAccountMergeService) {
        this.firebaseService = firebaseService;
        this.firebaseAccountMergeService = firebaseAccountMergeService;
    }

    /**
     * Links authenticated user to one of the Identity providers, like Google
     * Also merges the provider data like email, verification status, and photo into user account
     * NOTE: Only to be called from an authenticated endpoint
     * @param token String: Firebase Authentication Token
     */
    public void linkAccount(UserAuth userAuth, String token) {
        if (!firebaseService.canProceed())
            return;

        try {
            FirebaseToken decodedToken = FirebaseService.getToken(token);
            log.info(decodedToken.getClaims().toString());
            firebaseAccountMergeService.mergeFirebaseDetails(userAuth, decodedToken);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error linking data", e);
        }
    }

}
