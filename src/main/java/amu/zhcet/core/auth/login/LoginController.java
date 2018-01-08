package amu.zhcet.core.auth.login;

import amu.zhcet.core.auth.Auditor;
import amu.zhcet.core.auth.login.handler.RoleWiseSuccessHandler;
import amu.zhcet.data.user.UserService;
import amu.zhcet.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

    private final UserService userService;
    private final LoginAttemptService loginAttemptService;

    @Autowired
    public LoginController(UserService userService, LoginAttemptService loginAttemptService) {
        this.userService = userService;
        this.loginAttemptService = loginAttemptService;
    }

    @RequestMapping("/login")
    public String getLoginPage(Model model, @RequestParam(required = false) String error, HttpServletRequest request) {
        Authentication authentication = Auditor.getLoggedInAuthentication().get();
        if (SecurityUtils.isRememberMe(authentication))
            model.addAttribute("remember_error", "Please refresh your login");
        else if (SecurityUtils.isFullyAuthenticated(authentication))
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

}
