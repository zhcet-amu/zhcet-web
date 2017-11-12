package in.ac.amu.zhcet.service.firebase;

import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.data.repository.UserRepository;
import in.ac.amu.zhcet.service.user.CustomUser;
import in.ac.amu.zhcet.service.user.UserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Slf4j
@Service
@Transactional
public class FirebaseUserService {

    private static final UserToken UNAUTHENTICATED = new UserToken();

    private final FirebaseService firebaseService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public FirebaseUserService(FirebaseService firebaseService, UserRepository userRepository, ModelMapper modelMapper) {
        this.firebaseService = firebaseService;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    static UserToken getUnauthenticated() {
        return UNAUTHENTICATED;
    }

    UserToken fromUser(CustomUser user, String token) {
        if (user == null) {
            return UNAUTHENTICATED;
        }

        UserToken information = modelMapper.map(user, UserToken.class);
        information.setToken(token);
        information.setAuthenticated(true);
        return information;
    }

    @Async
    public void createUser(UserAuth userAuth) {
        if (!firebaseService.canProceed())
            return;

        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setUid(userAuth.getUserId())
                .setDisplayName(userAuth.getName());

        try {
            UserRecord userRecord = FirebaseAuth.getInstance().createUserAsync(request).get();
            log.info("Successfully created new user {}", userRecord.getUid());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error creating user on Firebase", e);
        }
    }

    private void setIfNotNull(String string, Consumer<String> consumer) {
        if (Strings.isNullOrEmpty(string))
            return;

        consumer.accept(string);
    }

    @Async
    public void updateUser(UserAuth userAuth) {
        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(userAuth.getUserId())
                .setEmailVerified(userAuth.isEmailVerified())
                .setDisplayName(userAuth.getName())
                .setPhotoUrl(userAuth.getDetails().getOriginalAvatarUrl())
                .setDisabled(!userAuth.isEnabled());

        setIfNotNull(userAuth.getEmail(), request::setEmail);
        setIfNotNull(userAuth.getDetails().getPhoneNumbers(), request::setPhoneNumber);

        try {
            UserRecord userRecord = FirebaseAuth.getInstance().updateUserAsync(request).get();
            log.info("Successfully updated user {}", userRecord.getUid());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error updating user on Firebase", e);
        }
    }

    private void mergeMail(UserAuth userAuth, FirebaseToken token) {
        if (Strings.isNullOrEmpty(token.getEmail()))
            return;

        UserAuth user = userRepository.findByEmail(token.getEmail());

        if (user != null && !user.getUserId().equals(userAuth.getUserId()))
            return;

        if (userAuth.isEmailVerified() && (userAuth.getEmail() != null && !userAuth.getEmail().equals(token.getEmail()))) {
            log.info("User email is already verified, skipping mail merge");
            return;
        }

        if (token.isEmailVerified()) {
            userAuth.setEmail(token.getEmail());
            userAuth.setEmailVerified(true);
        } else if (Strings.isNullOrEmpty(userAuth.getEmail())) {
            userAuth.setEmail(token.getEmail());
            userAuth.setEmailVerified(false);
        }
    }

    @Async
    public void mergeFirebaseDetails(UserAuth userAuth, FirebaseToken token) {
        if (userAuth == null || token == null)
            return;

        if (token.getClaims() != null)
            userAuth.getDetails().setFirebaseClaims(token.getClaims().toString());

        mergeMail(userAuth, token);

        if (Strings.isNullOrEmpty(userAuth.getDetails().getAvatarUrl()) && !Strings.isNullOrEmpty(token.getPicture())) {
            userAuth.getDetails().setOriginalAvatarUrl(token.getPicture());
            userAuth.getDetails().setAvatarUrl(token.getPicture());
            UserDetailService.updateStaticPrincipal(userAuth);
        }

        userRepository.save(userAuth);
        log.info("Merged firebase details into user account");
    }
}
