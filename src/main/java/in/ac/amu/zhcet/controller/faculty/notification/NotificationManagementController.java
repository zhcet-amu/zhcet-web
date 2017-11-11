package in.ac.amu.zhcet.controller.faculty.notification;

import in.ac.amu.zhcet.data.model.notification.Notification;
import in.ac.amu.zhcet.service.notification.NotificationManagementService;
import in.ac.amu.zhcet.utils.NotificationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class NotificationManagementController {

    private final NotificationManagementService notificationManagementService;

    @Autowired
    public NotificationManagementController(NotificationManagementService notificationManagementService) {
        this.notificationManagementService = notificationManagementService;
    }

    @GetMapping("/notification/manage")
    public String manageNotifications(@RequestParam(required = false) Integer page, Model model) {
        model.addAttribute("page_title", "Manage Notifications");
        model.addAttribute("page_subtitle", "Notification Manager");
        model.addAttribute("page_description", "Manage and monitor sent notifications");

        int currentPage = NotificationUtils.normalizePage(page);
        Page<Notification> notificationPage = notificationManagementService.getNotifications(currentPage);

        NotificationUtils.prepareNotifications(model, notificationPage, currentPage);
        List<Notification> notifications = notificationPage.getContent();
        notificationManagementService.setInformation(notifications);
        model.addAttribute("notifications", notifications);

        return "faculty/manage_notifications";
    }

    @PreAuthorize("createdNotification(#notification)")
    @GetMapping("/notification/{notification}/report")
    public String notificationReport(@RequestParam(required = false) Integer page, @PathVariable Notification notification, Model model) {
        model.addAttribute("page_title", "Notification Report");
        model.addAttribute("page_subtitle", "Notification Manager");
        model.addAttribute("page_description", "View notification receipt");

        model.addAttribute("notification", notification);

        return "faculty/notification_report";
    }

    @PreAuthorize("createdNotification(#notification)")
    @GetMapping("/notification/{notification}/delete")
    public String deleteNotification(@RequestParam(required = false) Integer page, @PathVariable Notification notification, RedirectAttributes redirectAttributes) {
        int currentPage = NotificationUtils.normalizePage(page);

        notificationManagementService.deleteNotification(notification);
        redirectAttributes.addFlashAttribute("notification_success", "Notification Deleted");
        return "redirect:/notification/manage?page=" + currentPage;
    }

}
