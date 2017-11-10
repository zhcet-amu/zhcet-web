package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.notification.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface NotificationRepository extends PagingAndSortingRepository<Notification, Long> {

    Page<Notification> findBySenderUserId(String userId, Pageable page);

}
