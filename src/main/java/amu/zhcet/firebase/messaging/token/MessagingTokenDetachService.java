package amu.zhcet.firebase.messaging.token;

import amu.zhcet.data.user.detail.UserDetailRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.internal.util.Assert;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessagingTokenDetachService {

    private final UserDetailRepository userDetailRepository;

    public MessagingTokenDetachService(UserDetailRepository userDetailRepository) {
        this.userDetailRepository = userDetailRepository;
    }

    public void detachToken(@NonNull String token) {
        Assert.notNull(token);
        userDetailRepository
                .findByFcmToken(token)
                .map(userDetail -> {
                    log.warn("Deleting FCM token {} for user {}", token, userDetail.getUserId());
                    userDetail.setFcmToken(null);
                    return userDetail;
                }).ifPresent(userDetailRepository::save);
    }
}
