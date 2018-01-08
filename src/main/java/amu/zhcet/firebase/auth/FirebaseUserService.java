package amu.zhcet.firebase.auth;

import amu.zhcet.core.auth.CustomUser;
import amu.zhcet.core.auth.UserDetailService;
import amu.zhcet.core.notification.ChannelType;
import amu.zhcet.core.notification.Notification;
import amu.zhcet.core.notification.sending.NotificationSendingService;
import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserType;
import amu.zhcet.firebase.FirebaseService;
import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.transaction.Transactional;
import java.util.Optional;
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

    /**
     * Merges firebase data into user account
     *
     * - Saves token claims in database
     * - Merges email and verification status from token
     * - Updates profile picture from provider data
     *
     * Mail merge and avatar update is done only if the user account information is not already present
     * @param user User Account the data is to be merged in
     * @param token Decoded Firebase Token containing data
     */
    @Async
    public void mergeFirebaseDetails(@Nullable User user, @Nullable FirebaseToken token) {
        if (user == null || token == null || !firebaseService.canProceed())
            return;

        if (token.getClaims() != null)
            user.getDetails().setFirebaseClaims(token.getClaims().toString());

        mergeMail(user, token);

        if (Strings.isNullOrEmpty(user.getDetails().getAvatarUrl()) && !Strings.isNullOrEmpty(token.getPicture())) {
            user.getDetails().setOriginalAvatarUrl(token.getPicture());
            user.getDetails().setAvatarUrl(token.getPicture());
            userDetailService.updatePrincipal(user);
        }

        userDetailService.getUserService().save(user);
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

    /**
     * Merges mail from firebase token into user account
     *
     * - If the email of user is already verified, process is skipped
     * - If the email in provider is already claimed by another user, that user's email
     *   and it's verification status is cleared and given to the passed in user.
     *   Previous user is notified of this revocation
     * - If the provider email is not verified, but account email is null, provider email is saved.
     *   But verification status is set to false
     * @param user User
     * @param token Firebase Token containing email information
     */
    private void mergeMail(User user, FirebaseToken token) {
        if (Strings.isNullOrEmpty(token.getEmail()))
            return;

        Optional<User> duplicate = userDetailService.getUserService().getUserByEmail(token.getEmail());

        // Exchange user emails if someone else has access to the email provided
        if (duplicate.isPresent() && !duplicate.get().getUserId().equals(user.getUserId())) {
            User duplicateUser = duplicate.get();
            log.warn("Another user account with same email exists, {} {} : {}", user.getUserId(), duplicateUser.getUserId(), token.getEmail());

            if (token.isEmailVerified()) {
                log.warn("New user has verified email, unconditionally exchanging emails from previous user");

                duplicateUser.setEmail(null);
                duplicateUser.setEmailVerified(false);

                userDetailService.getUserService().save(duplicateUser);
                userDetailService.getUserService().findById(duplicateUser.getUserId()).ifPresent(dupe -> {
                    log.info("Cleared email info from duplicate user, {}", dupe.getEmail());
                });
                sendEmailChangeNotification(duplicateUser, user, token.getEmail());
            }

        }

        if (user.isEmailVerified() && user.getEmail() != null && !user.getEmail().equals(token.getEmail())) {
            log.info("User email is already verified, skipping mail merge");
            return;
        }

        if (token.isEmailVerified()) {
            user.setEmail(token.getEmail());
            user.setEmailVerified(true);
        } else if (Strings.isNullOrEmpty(user.getEmail())) {
            user.setEmail(token.getEmail());
            user.setEmailVerified(false);
        }
    }

    private void sendEmailChangeNotification(User recipient, User claimant, String email) {
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

    private ChannelType fromUser(User user) {
        return user.getType() == UserType.STUDENT ? ChannelType.STUDENT : ChannelType.FACULTY;
    }
}
