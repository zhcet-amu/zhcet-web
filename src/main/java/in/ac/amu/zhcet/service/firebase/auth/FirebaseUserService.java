package in.ac.amu.zhcet.service.firebase.auth;

import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import in.ac.amu.zhcet.data.model.notification.ChannelType;
import in.ac.amu.zhcet.data.model.notification.Notification;
import in.ac.amu.zhcet.data.model.user.Type;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.service.firebase.FirebaseService;
import in.ac.amu.zhcet.service.notification.NotificationSendingService;
import in.ac.amu.zhcet.service.user.CustomUser;
import in.ac.amu.zhcet.service.user.UserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@Transactional
public class FirebaseUserService {

    private static final UserToken UNAUTHENTICATED = new UserToken();

    private final FirebaseService firebaseService;
    private final UserDetailService userDetailService;
    private final NotificationSendingService notificationSendingService;
    private final ModelMapper modelMapper;

    public FirebaseUserService(FirebaseService firebaseService, UserDetailService userDetailService, NotificationSendingService notificationSendingService, ModelMapper modelMapper) {
        this.firebaseService = firebaseService;
        this.userDetailService = userDetailService;
        this.notificationSendingService = notificationSendingService;
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

    private ChannelType fromUser(UserAuth userAuth) {
        return userAuth.getType() == Type.STUDENT ? ChannelType.STUDENT : ChannelType.FACULTY;
    }

    private void sendEmailChangeNotification(UserAuth recipient, UserAuth claimant, String email) {
        Notification notification = Notification.builder()
                .automated(true)
                .channelType(fromUser(recipient))
                .recipientChannel(recipient.getUserId())
                .sender(claimant)
                .title("Email Claimed")
                .message(String.format("Your previously set email **%s** is now claimed by `%s` *(%s)*.\n" +
                        "Please change it or claim it back by verifying it", email, claimant.getName(), claimant.getUserId()))
                .build();

        notificationSendingService.sendNotification(notification);
    }

    private void mergeMail(UserAuth userAuth, FirebaseToken token) {
        if (Strings.isNullOrEmpty(token.getEmail()))
            return;

        UserAuth duplicate = userDetailService.getUserService().getUserByEmail(token.getEmail());

        // Exchange user emails if someone else has access to the email provided
        if (duplicate != null && !duplicate.getUserId().equals(userAuth.getUserId())) {
            log.warn("Another user account with same email exists, {} {} : {}", userAuth.getUserId(), duplicate.getUserId(), token.getEmail());

            if (token.isEmailVerified()) {
                log.warn("New user has verified email, unconditionally exchanging emails from previous user");

                duplicate.setEmail(null);
                duplicate.setEmailVerified(false);

                userDetailService.getUserService().save(duplicate);
                log.info("Cleared email info from duplicate user, {}", userDetailService.getUserService().findById(duplicate.getUserId()).getEmail());
                sendEmailChangeNotification(duplicate, userAuth, token.getEmail());
            }

        }

        if (userAuth.isEmailVerified() && userAuth.getEmail() != null && !userAuth.getEmail().equals(token.getEmail())) {
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
        if (userAuth == null || token == null || !firebaseService.canProceed())
            return;

        if (token.getClaims() != null)
            userAuth.getDetails().setFirebaseClaims(token.getClaims().toString());

        mergeMail(userAuth, token);

        if (Strings.isNullOrEmpty(userAuth.getDetails().getAvatarUrl()) && !Strings.isNullOrEmpty(token.getPicture())) {
            userAuth.getDetails().setOriginalAvatarUrl(token.getPicture());
            userAuth.getDetails().setAvatarUrl(token.getPicture());
            UserDetailService.updateStaticPrincipal(userAuth);
        }

        userDetailService.getUserService().save(userAuth);
    }

    @Async
    public void getUser(String uid) {
        try {
            UserRecord userRecord = FirebaseAuth.getInstance().getUserAsync(uid).get();
            log.info(userRecord.toString());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
