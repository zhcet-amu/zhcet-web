package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.model.base.BaseUser;
import in.ac.amu.zhcet.data.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
        model.addAttribute("user", new BaseUser());
        model.addAttribute("users", userService.getAll());
        return "dean";
    }

    @PostMapping("/dean")
    public String enterUser(@ModelAttribute BaseUser user, @RequestParam("user_type") String userType) {
        user.setRoles(new String[]{userType});
        userService.saveUser(user);

        return "redirect:dean";
    }
}
