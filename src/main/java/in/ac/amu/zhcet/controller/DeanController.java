package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.model.base.user.UserAuth;
import in.ac.amu.zhcet.data.service.UserService;
import in.ac.amu.zhcet.data.service.user.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DeanController {

    private final UserService userService;

    @Autowired
    public DeanController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dean")
    public String deanAdmin(Model model) {
        model.addAttribute("user", new UserAuth());
        model.addAttribute("users", userService.getAll());

        CustomUser user = ((CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        return "dean";
    }

    @PostMapping("/dean")
    public String enterUser(@ModelAttribute UserAuth user, @RequestParam("user_type") String userType) {
        user.setRoles(new String[]{userType});
        userService.saveUser(user);

        return "redirect:dean";
    }
}
