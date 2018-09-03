package amu.zhcet.announcement.management;

import amu.zhcet.announcement.Announcement;
import amu.zhcet.announcement.AnnouncementRepository;
import amu.zhcet.auth.Auditor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
class AnnouncementManagementService {

    private static final int PAGE_SIZE = 10;

    private final AnnouncementRepository announcementRepository;

    @Autowired
    public AnnouncementManagementService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    public Page<Announcement> getAnnouncements(int page) {
        PageRequest pageRequest = PageRequest.of(page - 1, PAGE_SIZE, Sort.Direction.DESC, "sentTime");
        return announcementRepository.findBySenderUserId(Auditor.getLoggedInUsername(), pageRequest);
    }

    public void deleteAnnouncement(Announcement announcement) { announcementRepository.delete(announcement);}

    public void saveAnnouncement(Announcement announcement) { announcementRepository.save(announcement);}

}
