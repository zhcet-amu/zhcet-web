package in.ac.amu.zhcet.service.notification;

import in.ac.amu.zhcet.data.model.notification.NotificationRecipient;
import in.ac.amu.zhcet.data.repository.NotificationRecipientRepository;
import in.ac.amu.zhcet.service.user.Auditor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class NotificationReadingService {

    private static final int PAGE_SIZE = 10;

    private final NotificationRecipientRepository notificationRecipientRepository;

    @Autowired
    public NotificationReadingService(NotificationRecipientRepository notificationRecipientRepository) {
        this.notificationRecipientRepository = notificationRecipientRepository;
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
        List<NotificationRecipient> notificationRecipients = notificationRecipientRepository.findByRecipientUserIdAndSeen(userId, false);
        for (NotificationRecipient notificationRecipient : notificationRecipients) {
            notificationRecipient.setSeen(true);
            notificationRecipient.setReadTime(LocalDateTime.now());
        }
        notificationRecipientRepository.save(notificationRecipients);
    }

    public void markFavorite(NotificationRecipient notification) {
        notification.setFavorite(true);
        notificationRecipientRepository.save(notification);
    }

}
