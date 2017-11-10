package in.ac.amu.zhcet.service.notification;

import in.ac.amu.zhcet.data.model.notification.Notification;
import in.ac.amu.zhcet.data.model.notification.NotificationRecipient;
import in.ac.amu.zhcet.data.repository.NotificationRepository;
import in.ac.amu.zhcet.service.user.Auditor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class NotificationManagementService {

    private static final int PAGE_SIZE = 10;

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationManagementService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Page<Notification> getNotifications(int page) {
        PageRequest pageRequest = new PageRequest(page - 1, PAGE_SIZE, Sort.Direction.DESC, "sentTime");
        return notificationRepository.findBySenderUserId(Auditor.getLoggedInUsername(), pageRequest);
    }

    public void setInformation(List<Notification> notifications) {
        for (Notification notification : notifications) {
            notification.setSeenCount(getSeenCount(notification));
        }
    }

    private int getSeenCount(Notification notification) {
        return (int) notification.getNotificationRecipients()
                .stream()
                .filter(NotificationRecipient::isSeen)
                .count();
    }

    public void deleteNotification(Notification notification) {
        notificationRepository.delete(notification);
    }

    public void saveNotification(Notification notification) {
        notificationRepository.save(notification);
    }
}
