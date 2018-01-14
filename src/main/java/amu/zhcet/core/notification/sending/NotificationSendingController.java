package amu.zhcet.core.notification.sending;

import amu.zhcet.core.notification.ChannelType;
import amu.zhcet.core.notification.Notification;
import amu.zhcet.data.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Arrays;

@Slf4j
@Controller
@RequestMapping("/management/notification/send")
public class NotificationSendingController {

    private final UserService userService;
    private final NotificationSendingService notificationSendingService;

    public NotificationSendingController(UserService userService, NotificationSendingService notificationSendingService) {
        this.userService = userService;
        this.notificationSendingService = notificationSendingService;
    }

    @GetMapping
    public String sendNotification(Model model) {
        userService.getLoggedInUser().ifPresent(user -> {
            model.addAttribute("page_title", "Send Notifications");
            model.addAttribute("page_subtitle", "Notification Manager");
            model.addAttribute("page_description", "Send notifications to students, sections or departments");

            model.addAttribute("channel_types", Arrays.asList(
                    ChannelType.STUDENT, ChannelType.COURSE, ChannelType.TAUGHT_COURSE,
                    ChannelType.SECTION, ChannelType.FACULTY
            ));

            if (!model.containsAttribute("notification")) {
                Notification notification = new Notification();
                notification.setSender(user);
                model.addAttribute("notification", notification);
            }
        });

        return "management/send_notification";
    }

    @PostMapping
    public String handleSentNotification(@Valid Notification notification, BindingResult bindingResult, RedirectAttributes redirectAttribute) {
        String redirectUrl = "redirect:/management/notification/send";

        if (bindingResult.hasErrors()) {
            redirectAttribute.addFlashAttribute("notification", notification);
            redirectAttribute.addFlashAttribute("org.springframework.validation.BindingResult.notification", bindingResult);
            return redirectUrl;
        }

        userService.getLoggedInUser().ifPresent(user -> {
            notification.setSender(user);
            notificationSendingService.sendNotification(notification);
            redirectAttribute.addFlashAttribute("notification_success", "Notification sending in background");

        });

        return redirectUrl;
    }

}
