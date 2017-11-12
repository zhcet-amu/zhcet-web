package in.ac.amu.zhcet.service.firebase;

import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.service.user.Auditor;
import in.ac.amu.zhcet.service.user.CustomUser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Slf4j
@Service
public class FirebaseAuthService {

    private static final UserToken UNAUTHENTICATED = new UserToken();

    private final FirebaseService firebaseService;
    private final ModelMapper modelMapper;

    @Data
    public static class UserToken {
        private String token;
        private String username;
        private String name;
        private String avatar;
        private String type;
        private String departmentName;
        private boolean authenticated;
    }

    @Autowired
    public FirebaseAuthService(FirebaseService firebaseService, ModelMapper modelMapper) {
        this.firebaseService = firebaseService;
        this.modelMapper = modelMapper;
    }

    private UserToken fromUser(CustomUser user, String token) {
        if (user == null) {
            return UNAUTHENTICATED;
        }

        UserToken information = modelMapper.map(user, UserToken.class);
        information.setToken(token);
        information.setAuthenticated(true);
        return information;
    }

    public UserToken generateToken() {
        if (!firebaseService.canProceed())
            return null;

        try {
            CustomUser user = Auditor.getLoggedInUser();
            Map<String, Object> claims = new HashMap<>();
            claims.put("type", user.getType());
            claims.put("department", user.getDepartment().getName());
            String token = FirebaseAuth.getInstance().createCustomTokenAsync(user.getUsername(), claims).get();
            return fromUser(user, token);
        } catch (InterruptedException | ExecutionException e) {
            return UNAUTHENTICATED;
        }
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
}
