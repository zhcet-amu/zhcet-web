package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.configuration.security.login.RoleWiseSuccessHandler;
import in.ac.amu.zhcet.service.UserService;
import in.ac.amu.zhcet.service.user.Auditor;
import in.ac.amu.zhcet.service.security.login.LoginAttemptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class ViewController {

    private final UserService userService;
    private final LoginAttemptService loginAttemptService;

    @Autowired
    public ViewController(UserService userService, LoginAttemptService loginAttemptService) {
        this.userService = userService;
        this.loginAttemptService = loginAttemptService;
    }

    @RequestMapping("/login")
    public String getLoginPage(Model model, @RequestParam(required = false) String error, HttpServletRequest request) {
        Authentication authentication = Auditor.getLoggedInAuthentication();
        if (loginAttemptService.isRememberMe(authentication))
            model.addAttribute("remember_error", "Please refresh your login");
        else if (loginAttemptService.isFullyAuthenticated(authentication))
            return homePage();

        if (error != null) {
            model.addAttribute("login_error", loginAttemptService.getErrorMessage(request));
        }

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

    @GetMapping("/terms")
    public String getTerms() {
        return "terms_of_service";
    }

    @GetMapping("/privacy")
    public String getPrivacyPolicy() {
        return "privacy_policy";
    }

}
