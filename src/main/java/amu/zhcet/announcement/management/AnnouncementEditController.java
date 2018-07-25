package amu.zhcet.announcement.management;

import amu.zhcet.announcement.Announcement;
import amu.zhcet.common.utils.NotificationUtils;
import amu.zhcet.core.error.ErrorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/management/announcements/{announcement}/edit")
public class AnnouncementEditController {

    private final AnnouncementManagementService announcementManagementService;

    @Autowired
    public AnnouncementEditController (AnnouncementManagementService announcementManagementService) {
        this.announcementManagementService = announcementManagementService;
    }

    @GetMapping
    public String editAnnouncement(@PathVariable Announcement announcement, Model model) {
        ErrorUtils.requireNonNullAnnouncement(announcement);

        model.addAttribute("page_title", "Edit Announcement");
        model.addAttribute("page_subtitle", "Announcement Manager");
        model.addAttribute("page_description", "Edit sent Announcement");

        if (!model.containsAttribute("announcement"))
            model.addAttribute("announcement", announcement);
        return "management/edit_announcement";
    }

    @PostMapping
    public String saveEditAnnouncement(@RequestParam(required = false) Integer page, @PathVariable Announcement announcement,
                                       @Valid Announcement edited, BindingResult result,
                                       RedirectAttributes redirectAttributes)
    {
        ErrorUtils.requireNonNullAnnouncement(announcement);
        int currentPage = NotificationUtils.normalizePage(page);

        String redirectUrl = String.format("redirect:/management/announcements/%d/edit?page=%d", announcement.getId(), currentPage);

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("announcement", edited);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.announcement", result);
            return redirectUrl;
        }

        announcement.setTitle(edited.getTitle());
        announcement.setMessage(edited.getMessage());
        announcementManagementService.saveAnnouncement(announcement);
        redirectAttributes.addFlashAttribute("announcement_success", "Announcement Edited");
        return "redirect:/management/announcements?page=" + currentPage;
    }
}
