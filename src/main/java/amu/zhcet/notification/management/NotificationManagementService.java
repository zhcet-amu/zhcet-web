package amu.zhcet.notification.management;

import amu.zhcet.auth.Auditor;
import amu.zhcet.notification.Notification;
import amu.zhcet.notification.NotificationRepository;
import amu.zhcet.notification.recipient.NotificationRecipient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
class NotificationManagementService {

    private static final int PAGE_SIZE = 10;

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationManagementService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Page<Notification> getNotifications(int page) {
        PageRequest pageRequest = PageRequest.of(page - 1, PAGE_SIZE, Sort.Direction.DESC, "sentTime");
        return notificationRepository.findBySenderUserId(Auditor.getLoggedInUsername(), pageRequest);
    }

    public void setInformation(List<Notification> notifications) {
        notifications.forEach(this::setSeenCount);
    }

    public void setSeenCount(Notification notification) {
        notification.setSeenCount(getSeenCount(notification));
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
