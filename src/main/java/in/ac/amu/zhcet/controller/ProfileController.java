package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.base.user.Type;
import in.ac.amu.zhcet.data.model.base.user.UserAuth;
import in.ac.amu.zhcet.data.model.base.user.UserDetail;
import in.ac.amu.zhcet.data.model.token.VerificationToken;
import in.ac.amu.zhcet.data.service.FacultyService;
import in.ac.amu.zhcet.data.service.StudentService;
import in.ac.amu.zhcet.data.service.UserService;
import in.ac.amu.zhcet.data.service.token.DuplicateEmailException;
import in.ac.amu.zhcet.data.service.token.EmailVerificationService;
import in.ac.amu.zhcet.data.service.user.UserDetailService;
import in.ac.amu.zhcet.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class ProfileController {

    private final UserService userService;
    private final StudentService studentService;
    private final FacultyService facultyService;
    private final UserDetailService userDetailService;
    private final EmailVerificationService emailVerificationService;

    @Autowired
    public ProfileController(UserService userService, StudentService studentService, FacultyService facultyService, UserDetailService userDetailService, EmailVerificationService emailVerificationService) {
        this.userService = userService;
        this.studentService = studentService;
        this.facultyService = facultyService;
        this.userDetailService = userDetailService;
        this.emailVerificationService = emailVerificationService;
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        UserAuth userAuth = userService.getLoggedInUser();
        model.addAttribute("user", userAuth);
        model.addAttribute("user_details", userAuth.getDetails());

        if (userAuth.getType().equals(Type.STUDENT)) {
            Student student = studentService.getLoggedInStudent();
            model.addAttribute("student", student);
        } else {
            FacultyMember facultyMember = facultyService.getLoggedInMember();
            model.addAttribute("faculty", facultyMember);
        }

        return "profile";
    }

    private void sendVerificationLink(String email, String appUrl, RedirectAttributes redirectAttributes) {
        try {
            VerificationToken token = emailVerificationService.generate(email);
            emailVerificationService.sendMail(appUrl, token);
            redirectAttributes.addFlashAttribute("link_sent", "Verification link sent to '" + email + "'!");
        } catch (DuplicateEmailException de) {
            redirectAttributes.addFlashAttribute("duplicate_email", de.getMessage());
        }
    }

    @PostMapping("/profile/register_email")
    public String registerEmail(RedirectAttributes redirectAttributes, @RequestParam String email, HttpServletRequest request) {
        if (Utils.isValidEmail(email)) {
            sendVerificationLink(email, Utils.getAppUrl(request), redirectAttributes);
        } else {
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("invalid_email", "The provided email is invalid!");
        }

        return "redirect:/profile";
    }

    @PostMapping("/profile/confirm_email")
    public String registerEmail(RedirectAttributes redirectAttributes, HttpServletRequest request) {
        UserAuth user = userService.getLoggedInUser();
        String email = user.getEmail();

        if (Utils.isValidEmail(user.getEmail())) {
            sendVerificationLink(email, Utils.getAppUrl(request), redirectAttributes);
        } else {
            redirectAttributes.addFlashAttribute("invalid_email", "The provided email is invalid!");
        }

        return "redirect:/profile";
    }

    @GetMapping("/login/verify")
    public String resetPassword(Model model, @RequestParam("token") String token){
        String result = emailVerificationService.validate(token);
        if (result != null) {
            model.addAttribute("error", result);
        } else {
            emailVerificationService.confirmEmail(token);
            model.addAttribute("success", "Your email was successfully verified!");
        }
        return "verify_email";
    }

    @PostMapping("/profile/details")
    public String saveProfile(@ModelAttribute UserDetail userDetail, final RedirectAttributes redirectAttributes) {
        log.info(userDetail.toString());


        try {
            userDetailService.updateDetails(userService.getLoggedInUser(), userDetail);
            redirectAttributes.addFlashAttribute("success", true);
        } catch (Exception exc) {
            exc.printStackTrace();
            List<String> errors = new ArrayList<>();

            redirectAttributes.addFlashAttribute("errors", errors);
        }

        return "redirect:/profile";
    }

}
