package in.ac.amu.zhcet.controller.faculty;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
public class SendNotificationController {

    @GetMapping("/notification/send")
    public String sendNotification(Model model) {
        model.addAttribute("page_title", "Send Notifications");
        model.addAttribute("page_subtitle", "Notification Manager");
        model.addAttribute("page_description", "Send notifications to students, sections or departments");

        return "faculty/send_notification";
    }

    @PostMapping("/notification/send")
    public String handleSentNotification(RedirectAttributes redirectAttributes, @RequestParam String notification) {
        redirectAttributes.addFlashAttribute("input", notification);

        return "redirect:/notification/send";
    }

}
