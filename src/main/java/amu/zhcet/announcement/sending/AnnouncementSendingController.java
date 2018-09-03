package amu.zhcet.announcement.sending;

import amu.zhcet.announcement.Announcement;
import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/management/announcement/send")
public class AnnouncementSendingController {

    private final UserService userService;
    private final AnnouncementSendingService announcementSendingService;

    public AnnouncementSendingController(UserService userService, AnnouncementSendingService announcementSendingService) {
        this.userService = userService;
        this.announcementSendingService = announcementSendingService;
    }

    @GetMapping
    public String sendAnnouncement(Model model) {
        User user = userService.getLoggedInUser().orElseThrow(() -> new AccessDeniedException("403"));

        model.addAttribute("page_title", "Send Announcements");
        model.addAttribute("page_subtitle", "Announcement Manager");
        model.addAttribute("page_description", "Send announcements to everyone");

        if (!model.containsAttribute("announcement")) {
            Announcement announcement = new Announcement();
            announcement.setSender(user);
            model.addAttribute("announcement", announcement);
        }

        return "management/send_announcement";
    }

    @PostMapping
    public String handleSentAnnouncement(@Valid Announcement announcement, BindingResult bindingResult, RedirectAttributes redirectAttribute) {
        User user = userService.getLoggedInUser().orElseThrow(() -> new AccessDeniedException("403"));

        if (bindingResult.hasErrors()) {
            redirectAttribute.addFlashAttribute("announcement", announcement);
            redirectAttribute.addFlashAttribute("org.springframework.validation.BindingResult.announcement", bindingResult);
        } else {
            announcement.setSender(user);
            announcementSendingService.sendAnnouncement(announcement);
            redirectAttribute.addFlashAttribute("announcement_success", "Announcement sending in background");
        }

        return "redirect:/management/announcement/send";
    }

}
