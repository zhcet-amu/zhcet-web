package amu.zhcet.core.notification.management;

import amu.zhcet.common.utils.NotificationUtils;
import amu.zhcet.core.notification.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Slf4j
@Controller
public class NotificationEditController {

    private final NotificationManagementService notificationManagementService;

    @Autowired
    public NotificationEditController(NotificationManagementService notificationManagementService) {
        this.notificationManagementService = notificationManagementService;
    }

    @GetMapping("/management/notifications/{notification}/edit")
    public String editNotification(@PathVariable Notification notification, Model model) {
        String templateUrl = "management/edit_notification";
        if (notification == null)
            return templateUrl;

        model.addAttribute("page_title", "Edit Notification");
        model.addAttribute("page_subtitle", "Notification Manager");
        model.addAttribute("page_description", "Edit sent notifications");

        if (!model.containsAttribute("notification"))
            model.addAttribute("notification", notification);
        return templateUrl;
    }

    @PostMapping("/management/notifications/{notification}/edit")
    public String saveEditNotification(@RequestParam(required = false) Integer page, @PathVariable Notification notification,
                                       @Valid Notification edited, BindingResult result,
                                       RedirectAttributes redirectAttributes)
    {
        int currentPage = NotificationUtils.normalizePage(page);
        if (notification == null)
            return "redirect:/management/notifications?page=" + currentPage;

        String redirectUrl = String.format("redirect:/management/notifications/%d/edit?page=%d", notification.getId(), currentPage);

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("notification", edited);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.notification", result);
            return redirectUrl;
        }

        notification.setTitle(edited.getTitle());
        notification.setMessage(edited.getMessage());
        notificationManagementService.saveNotification(notification);
        redirectAttributes.addFlashAttribute("notification_success", "Notification Edited");
        return "redirect:/management/notifications?page=" + currentPage;
    }

}
