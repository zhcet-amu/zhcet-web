package in.ac.amu.zhcet.controller.faculty;

import in.ac.amu.zhcet.data.model.notification.ChannelType;
import in.ac.amu.zhcet.data.model.notification.Notification;
import in.ac.amu.zhcet.service.UserService;
import in.ac.amu.zhcet.service.notification.NotificationSendingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Arrays;

@Slf4j
@Controller
public class SendNotificationController {

    private final UserService userService;
    private final NotificationSendingService notificationSendingService;

    public SendNotificationController(UserService userService, NotificationSendingService notificationSendingService) {
        this.userService = userService;
        this.notificationSendingService = notificationSendingService;
    }

    @GetMapping("/notification/send")
    public String sendNotification(Model model) {
        model.addAttribute("page_title", "Send Notifications");
        model.addAttribute("page_subtitle", "Notification Manager");
        model.addAttribute("page_description", "Send notifications to students, sections or departments");

        model.addAttribute("channel_types", Arrays.asList(ChannelType.STUDENT, ChannelType.COURSE));

        if (!model.containsAttribute("notification")) {
            Notification notification = new Notification();
            notification.setSender(userService.getLoggedInUser());
            model.addAttribute("notification", notification);
        }

        return "faculty/send_notification";
    }

    @PostMapping("/notification/send")
    public String handleSentNotification(@Valid Notification notification, BindingResult bindingResult, RedirectAttributes redirectAttribute) {
        String redirectUrl = "redirect:/notification/send";

        if (bindingResult.hasErrors()) {
            redirectAttribute.addFlashAttribute("notification", notification);
            redirectAttribute.addFlashAttribute("org.springframework.validation.BindingResult.notification", bindingResult);
            return redirectUrl;
        }

        notification.setSender(userService.getLoggedInUser());
        notificationSendingService.sendNotification(notification);
        redirectAttribute.addFlashAttribute("notification_success", "Notification sending in background");

        return redirectUrl;
    }

}
