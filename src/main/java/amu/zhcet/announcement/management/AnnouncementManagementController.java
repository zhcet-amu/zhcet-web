package amu.zhcet.announcement.management;

import amu.zhcet.announcement.Announcement;
import amu.zhcet.common.utils.NotificationUtils;
import amu.zhcet.core.error.ErrorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/management/announcements")
public class AnnouncementManagementController {

    private final AnnouncementManagementService announcementManagementService;

    @Autowired
    public AnnouncementManagementController(AnnouncementManagementService announcementManagementService) {
        this.announcementManagementService = announcementManagementService;
    }

    @GetMapping
    public String manageAnnouncements(@RequestParam(required = false) Integer page, Model model) {
        model.addAttribute("page_title", "Manage Announcements");
        model.addAttribute("page_subtitle", "Announcement Manager");
        model.addAttribute("page_description", "Manage and monitor sent announcements");

        int currentPage = NotificationUtils.normalizePage(page);
        Page<Announcement> announcementPage = announcementManagementService.getAnnouncements(currentPage);

        NotificationUtils.prepareNotifications(model, announcementPage, currentPage);
        List<Announcement> announcements = announcementPage.getContent();
        model.addAttribute("announcements", announcements);

        return "management/manage_announcements";
    }

    @GetMapping("/{announcement}/delete")
    public String deleteAnnouncement(@RequestParam(required = false) Integer page, @PathVariable Announcement announcement, RedirectAttributes redirectAttributes) {
        ErrorUtils.requireNonNullAnnouncement(announcement);
        int currentPage = NotificationUtils.normalizePage(page);

        announcementManagementService.deleteAnnouncement(announcement);
        redirectAttributes.addFlashAttribute("announcement_success", "Announcement Deleted");
        return "redirect:/management/announcements?page=" + currentPage;
    }
}
