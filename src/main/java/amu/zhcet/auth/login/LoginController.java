package amu.zhcet.auth.login;

import amu.zhcet.auth.Auditor;
import amu.zhcet.auth.login.handler.RoleWiseSuccessHandler;
import amu.zhcet.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class LoginController {

    private final LoginAttemptService loginAttemptService;

    @Autowired
    public LoginController(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @GetMapping("/login")
    public String getLoginPage(Model model, @RequestParam(required = false) String error, HttpServletRequest request) {
        Authentication authentication = Auditor.getLoggedInAuthentication().orElse(null);

        if (SecurityUtils.isRememberMe(authentication))
            model.addAttribute("remember_error", "Please refresh your login");
        else if (SecurityUtils.isFullyAuthenticated(authentication))
            return homePage();

        SecurityUtils.clearStaleAuthorities(authentication);

        if (error != null) {
            loginAttemptService.addErrors(model, request);
        }

        return "user/login";
    }

    @RequestMapping("/logout")
    public String getLogoutPage() {
        String logoutUrl = "redirect:/login";
        if (!Auditor.getLoggedInAuthentication().isPresent())
            return logoutUrl;

        return logoutUrl + "?logout";
    }

    @RequestMapping(value = {"/", ""})
    public String homePage() {
        Authentication authentication = Auditor.getLoggedInAuthentication().orElse(null);
        SecurityUtils.clearStaleAuthorities(authentication);

        if (authentication == null)
            return "redirect:/login";

        return "redirect:" + RoleWiseSuccessHandler.determineTargetUrl(authentication);
    }

}
