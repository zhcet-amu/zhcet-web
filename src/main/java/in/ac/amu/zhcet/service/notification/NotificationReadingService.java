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

@Slf4j
@Service
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
}
