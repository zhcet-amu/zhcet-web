package in.ac.amu.zhcet.controller.user.notification;

import in.ac.amu.zhcet.data.model.notification.NotificationRecipient;
import in.ac.amu.zhcet.service.notification.NotificationReadingService;
import in.ac.amu.zhcet.utils.NotificationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
public class NotificationController {

    private final NotificationReadingService notificationReadingService;

    @Autowired
    public NotificationController(NotificationReadingService notificationReadingService) {
        this.notificationReadingService = notificationReadingService;
    }

    private static void prepareModel(Model model) {
        model.addAttribute("page_title", "Notifications");
        model.addAttribute("page_subtitle", "Notification Manager");
        model.addAttribute("page_description", "View and manage notifications");
    }

    @GetMapping("/notifications")
    public String getNotifications(@RequestParam(required = false) Integer page, Model model) {
        int currentPage = NotificationUtils.normalizePage(page);
        Page<NotificationRecipient> notificationRecipientPage = notificationReadingService.getNotifications(currentPage);

        prepareModel(model);
        NotificationUtils.prepareNotifications(model, notificationRecipientPage, currentPage);
        model.addAttribute("notifications", notificationRecipientPage.getContent());
        model.addAttribute("favorite_page", false);

        return "user/notifications";
    }

    @GetMapping("/notifications/favorite")
    public String getFavoriteNotifications(@RequestParam(required = false) Integer page, Model model) {
        int currentPage = NotificationUtils.normalizePage(page);
        Page<NotificationRecipient> notificationRecipientPage = notificationReadingService.getFavoriteNotifications(currentPage);

        prepareModel(model);
        NotificationUtils.prepareNotifications(model, notificationRecipientPage, currentPage);
        model.addAttribute("notifications", notificationRecipientPage.getContent());
        model.addAttribute("favorite_page", true);

        return "user/notifications";
    }

    @GetMapping("/notifications/mark/read")
    public String markRead(@RequestParam(required = false) Integer page, RedirectAttributes redirectAttributes) {
        int currentPage = NotificationUtils.normalizePage(page);
        notificationReadingService.markRead();
        redirectAttributes.addFlashAttribute("notification_success", "Marked all notifications as read");
        return "redirect:/notifications?page=" + currentPage;
    }

    @PreAuthorize("hasNotificationPermission(#notification)")
    @GetMapping("/notifications/mark/favorite/{notification}")
    public String markFavorite(@RequestParam(required = false) Integer page, @PathVariable NotificationRecipient notification, RedirectAttributes redirectAttributes) {
        int currentPage = NotificationUtils.normalizePage(page);

        notificationReadingService.markFavorite(notification);
        redirectAttributes.addFlashAttribute("notification_success", "Marked the notification as favorite");
        return "redirect:/notifications?page=" + currentPage;
    }

    @PreAuthorize("hasNotificationPermission(#notification)")
    @GetMapping("/notifications/unmark/favorite/{notification}")
    public String unmarkFavorite(@RequestParam(required = false) Integer page, @PathVariable NotificationRecipient notification, RedirectAttributes redirectAttributes) {
        int currentPage = NotificationUtils.normalizePage(page);

        notificationReadingService.unmarkFavorite(notification);
        redirectAttributes.addFlashAttribute("notification_success", "Unmarked the notification as favorite");
        return "redirect:/notifications?page=" + currentPage;
    }

}
