package in.ac.amu.zhcet.service.notification;

import in.ac.amu.zhcet.data.model.notification.NotificationRecipient;
import in.ac.amu.zhcet.data.repository.NotificationRecipientRepository;
import in.ac.amu.zhcet.service.user.Auditor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Service
@Transactional
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class NotificationReadingService {

    /* package private */ static final int PAGE_SIZE = 10;

    private final NotificationRecipientRepository notificationRecipientRepository;
    private final CachedNotificationService cachedNotificationService;

    @Autowired
    public NotificationReadingService(NotificationRecipientRepository notificationRecipientRepository, CachedNotificationService cachedNotificationService) {
        this.notificationRecipientRepository = notificationRecipientRepository;
        this.cachedNotificationService = cachedNotificationService;
    }

    public List<NotificationRecipient> getUnreadNotifications() {
        String userId = Auditor.getLoggedInUsername();
        return cachedNotificationService.getUnreadNotifications(userId).getContent();
    }

    public String getUnreadNotificationCount() {
        int size = getUnreadNotifications().size();
        return size + (size >= 10 ? "+" : "");
    }

    public Page<NotificationRecipient> getNotifications(int page) {
        String userId = Auditor.getLoggedInUsername();
        PageRequest pageRequest = new PageRequest(page - 1, PAGE_SIZE, Sort.Direction.DESC, "notification.sentTime");
        return notificationRecipientRepository.findByRecipientUserId(userId, pageRequest);
    }

    public Page<NotificationRecipient> getFavoriteNotifications(int page) {
        String userId = Auditor.getLoggedInUsername();
        PageRequest pageRequest = new PageRequest(page - 1, PAGE_SIZE, Sort.Direction.DESC, "notification.sentTime");
        return notificationRecipientRepository.findByRecipientUserIdAndFavorite(userId, true, pageRequest);
    }

    public void markRead() {
        String userId = Auditor.getLoggedInUsername();
        cachedNotificationService.markRead(userId);
    }

    public void markFavorite(NotificationRecipient notification) {
        notification.setFavorite(true);
        notificationRecipientRepository.save(notification);
    }

    public void unmarkFavorite(NotificationRecipient notification) {
        notification.setFavorite(false);
        notificationRecipientRepository.save(notification);
    }
}
