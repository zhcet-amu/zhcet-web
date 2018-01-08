package amu.zhcet.core.notification.recipient;

import amu.zhcet.core.notification.Notification;
import amu.zhcet.core.notification.NotificationRepository;
import amu.zhcet.core.notification.reading.NotificationReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CachedNotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;

    @Autowired
    CachedNotificationService(NotificationRepository notificationRepository, NotificationRecipientRepository notificationRecipientRepository) {
        this.notificationRepository = notificationRepository;
        this.notificationRecipientRepository = notificationRecipientRepository;
    }

    @Cacheable(value = "unread_notifications", key = "#userId")
    public Page<NotificationRecipient> getUnreadNotifications(String userId) {
        PageRequest pageRequest = new PageRequest(0, NotificationReadingService.PAGE_SIZE, Sort.Direction.DESC, "notification.sentTime");
        return notificationRecipientRepository.findByRecipientUserIdAndSeen(userId, false, pageRequest);
    }

    @CacheEvict(value = "unread_notifications", key = "#userId")
    public void markRead(String userId) {
        List<NotificationRecipient> notificationRecipients = notificationRecipientRepository.findByRecipientUserIdAndSeen(userId, false);
        for (NotificationRecipient notificationRecipient : notificationRecipients) {
            notificationRecipient.setSeen(true);
            notificationRecipient.setReadTime(LocalDateTime.now());
        }
        notificationRecipientRepository.save(notificationRecipients);
    }

    @CacheEvict(value = "unread_notifications", key = "#userId")
    public void resetUnreadCount(String userId) {
        // Do nothing
    }

    public void save(NotificationRecipient notificationRecipient) {
        notificationRecipientRepository.save(notificationRecipient);
    }

    public void save(Notification notification) {
        notificationRepository.save(notification);
    }
}
