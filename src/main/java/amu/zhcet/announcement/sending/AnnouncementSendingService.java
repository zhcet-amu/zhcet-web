package amu.zhcet.announcement.sending;

import amu.zhcet.announcement.Announcement;
import amu.zhcet.announcement.AnnouncementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
public class AnnouncementSendingService {

    private final AnnouncementRepository announcementRepository;

    @Autowired
    public AnnouncementSendingService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    private void sendAnnouncementOnly(Announcement announcement) {
        if (announcement.getSentTime() == null)
            announcement.setSentTime(LocalDateTime.now());
        announcementRepository.save(announcement);
    }

    @Async
    public void sendAnnouncement(Announcement announcement) {
        // Save the primary announcement in server
        sendAnnouncementOnly(announcement);
    }
}
