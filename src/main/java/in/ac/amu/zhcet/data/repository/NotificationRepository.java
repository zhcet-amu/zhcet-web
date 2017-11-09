package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.notification.Notification;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface NotificationRepository extends PagingAndSortingRepository<Notification, Long> {

    List<Notification> findBySenderOrderBySentTimeDesc(UserAuth userAuth, Pageable page);

}
