package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.notification.NotificationRecipient;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface NotificationRecipientRepository extends PagingAndSortingRepository<NotificationRecipient, Long> {

    List<NotificationRecipient> findByRecipientOrderByNotificationSentTimeDesc(UserAuth userAuth, Pageable pageable);

    List<NotificationRecipient> findByRecipientAndSeen(UserAuth userAuth, boolean read);

    List<NotificationRecipient> findByRecipientAndFavoriteOrderByNotificationSentTimeDesc(UserAuth userAuth, boolean favorite, Pageable pageable);

}
