package amu.zhcet.announcement.reading;

import amu.zhcet.announcement.Announcement;
import amu.zhcet.announcement.AnnouncementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@Transactional
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AnnouncementReadingService {

    public static final int PAGE_SIZE = 10;

    private final AnnouncementRepository announcementRepository;

    @Autowired
    public AnnouncementReadingService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    public Page<Announcement> getAnnouncements(int page) {
        PageRequest pageRequest = PageRequest.of(page - 1, PAGE_SIZE, Sort.Direction.DESC, "sentTime");
        return announcementRepository.findAll(pageRequest);
    }

}