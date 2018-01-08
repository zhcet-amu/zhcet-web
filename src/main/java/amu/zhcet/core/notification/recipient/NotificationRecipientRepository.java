package amu.zhcet.core.notification.recipient;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface NotificationRecipientRepository extends PagingAndSortingRepository<NotificationRecipient, Long> {

    Page<NotificationRecipient> findByRecipientUserId(String userId, Pageable pageable);

    List<NotificationRecipient> findByRecipientUserIdAndSeen(String userId, boolean read);

    Page<NotificationRecipient> findByRecipientUserIdAndSeen(String userId, boolean read, Pageable pageable);

    Page<NotificationRecipient> findByRecipientUserIdAndFavorite(String userId, boolean favorite, Pageable pageable);

}
