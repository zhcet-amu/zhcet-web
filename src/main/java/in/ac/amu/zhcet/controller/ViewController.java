package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.configuration.security.RoleWiseSuccessHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {

    @RequestMapping("/login")
    public String getLoginPage() {
        return "user/login";
    }

    @RequestMapping(value = {"/", ""})
    public String homePage() {
        return "redirect:" + RoleWiseSuccessHandler.determineTargetUrl(SecurityContextHolder.getContext().getAuthentication());
    }

}
