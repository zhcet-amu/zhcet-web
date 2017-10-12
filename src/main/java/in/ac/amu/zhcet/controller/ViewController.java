package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.configuration.security.RoleWiseSuccessHandler;
import in.ac.amu.zhcet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {

    private final UserService userService;

    @Autowired
    public ViewController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/login")
    public String getLoginPage() {
        if (userService.getLoggedInUser() != null)
            return homePage();

        return "user/login";
    }

    @RequestMapping(value = {"/", ""})
    public String homePage() {
        return "redirect:" + RoleWiseSuccessHandler.determineTargetUrl(SecurityContextHolder.getContext().getAuthentication());
    }

}
