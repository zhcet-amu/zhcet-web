package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.configuration.security.RoleWiseSuccessHandler;
import in.ac.amu.zhcet.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
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

    @RequestMapping("/logout")
    public String getLogoutPage() {
        String logoutUrl = "redirect:/login";
        if (userService.getLoggedInUser() == null)
            return logoutUrl;

        return logoutUrl + "?logout";
    }

    @RequestMapping(value = {"/", ""})
    public String homePage() {
        return "redirect:" + RoleWiseSuccessHandler.determineTargetUrl(SecurityContextHolder.getContext().getAuthentication());
    }

}
