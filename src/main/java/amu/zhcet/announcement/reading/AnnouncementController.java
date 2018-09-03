package amu.zhcet.announcement.reading;

import amu.zhcet.announcement.Announcement;
import amu.zhcet.common.utils.NotificationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/announcements")
public class AnnouncementController {

    private final AnnouncementReadingService announcementReadingService;

    @Autowired
    public AnnouncementController(AnnouncementReadingService announcementReadingService) {
        this.announcementReadingService = announcementReadingService;
    }

    private static void prepareModel(Model model) {
        model.addAttribute("page_title", "Announcements");
        model.addAttribute("page_subtitle", "Announcement Manager");
        model.addAttribute("page_description", "View and manage announcements");
    }

    @GetMapping
    public String getAnnouncements(@RequestParam(required = false) Integer page, Model model) {
        int currentPage = NotificationUtils.normalizePage(page);
        Page<Announcement> announcementPage = announcementReadingService.getAnnouncements(currentPage);

        prepareModel(model);
        NotificationUtils.prepareNotifications(model, announcementPage, currentPage);
        model.addAttribute("announcements", announcementPage.getContent());
        model.addAttribute("favorite_page", false);

        return "user/announcements";
    }

}
