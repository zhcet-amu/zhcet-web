package amu.zhcet.core.profile;

import amu.zhcet.data.user.Gender;
import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserService;
import amu.zhcet.data.user.UserType;
import amu.zhcet.data.user.detail.UserDetail;
import amu.zhcet.data.user.faculty.FacultyService;
import amu.zhcet.data.user.student.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final StudentService studentService;
    private final FacultyService facultyService;

    @Autowired
    public ProfileController(UserService userService, StudentService studentService, FacultyService facultyService) {
        this.userService = userService;
        this.studentService = studentService;
        this.facultyService = facultyService;
    }

    @GetMapping
    public String profile(Model model) {
        User user = userService.getLoggedInUser().orElseThrow(() -> new AccessDeniedException("403"));
        model.addAttribute("user", user);

        model.addAttribute("page_title", "Profile");

        if (user.getType().equals(UserType.STUDENT)) {
            studentService.getLoggedInStudent().ifPresent(student -> model.addAttribute("student", student));
        } else {
            facultyService.getLoggedInMember().ifPresent(facultyMember -> model.addAttribute("faculty", facultyMember));
        }

        return "user/profile";
    }

    @GetMapping("/settings")
    public String profileSettings(Model model) {
        User user = userService.getLoggedInUser().orElseThrow(() -> new AccessDeniedException("403"));
        model.addAttribute("user", user);

        if (!model.containsAttribute("user_details"))
            model.addAttribute("user_details", user.getDetails());

        model.addAttribute("page_title", "Profile Settings");
        model.addAttribute("page_subtitle", "Profile Settings for " + user.getName());
        model.addAttribute("page_description", "Manage Profile Details and Account");
        model.addAttribute("genders", Gender.values());

        if (user.getType().equals(UserType.STUDENT)) {
            studentService.getLoggedInStudent().ifPresent(student -> model.addAttribute("student", student));
        } else {
            facultyService.getLoggedInMember().ifPresent(facultyMember -> model.addAttribute("faculty", facultyMember));
        }
        return "user/edit_profile";
    }

    @PostMapping("/details")
    @PreAuthorize("@authService.isFullyAuthenticated(principal)")
    public String saveProfile(@ModelAttribute @Valid UserDetail userDetail, BindingResult result, RedirectAttributes redirectAttributes) {
        User user = userService.getLoggedInUser().orElseThrow(() -> new AccessDeniedException("403"));
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user_details", result);
            redirectAttributes.addFlashAttribute("user_details", userDetail);
        } else {
            userService.updateDetails(user, userDetail);
            redirectAttributes.addFlashAttribute("success", true);
        }

        return "redirect:/profile/settings";
    }

}
