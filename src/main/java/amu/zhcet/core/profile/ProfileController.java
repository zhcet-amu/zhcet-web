package amu.zhcet.core.profile;

import amu.zhcet.core.auth.UserDetailService;
import amu.zhcet.data.user.Gender;
import amu.zhcet.data.user.UserService;
import amu.zhcet.data.user.UserType;
import amu.zhcet.data.user.detail.UserDetail;
import amu.zhcet.data.user.faculty.FacultyService;
import amu.zhcet.data.user.student.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletRequest;
import javax.validation.Valid;

@Slf4j
@Controller
public class ProfileController {

    private final UserService userService;
    private final UserDetailService userDetailService;
    private final StudentService studentService;
    private final FacultyService facultyService;

    @Autowired
    public ProfileController(UserDetailService userDetailService, StudentService studentService, FacultyService facultyService) {
        this.userDetailService = userDetailService;
        this.userService = userDetailService.getUserService();
        this.studentService = studentService;
        this.facultyService = facultyService;
    }

    @GetMapping("/profile")
    public String profile(Model model, ServletRequest request) {
        userService.getLoggedInUser().ifPresent(user -> {
            model.addAttribute("user", user);

            if (!model.containsAttribute("user_details"))
                model.addAttribute("user_details", user.getDetails());

            model.addAttribute("page_title", "Profile");
            model.addAttribute("page_subtitle", "Profile Settings for " + user.getName());
            model.addAttribute("page_description", "Manage Profile Details and Information");
            model.addAttribute("genders", Gender.values());

            if (user.getType().equals(UserType.STUDENT)) {
                studentService.getLoggedInStudent().ifPresent(student -> model.addAttribute("student", student));
            } else {
                facultyService.getLoggedInMember().ifPresent(facultyMember -> model.addAttribute("faculty", facultyMember));
            }

            if (request.getParameterMap().containsKey("refresh"))
                userDetailService.updatePrincipal(user);
        });

        return "user/profile";
    }

    @PostMapping("/profile/details")
    public String saveProfile(@ModelAttribute @Valid UserDetail userDetail, BindingResult result, RedirectAttributes redirectAttributes) {
        userService.getLoggedInUser().ifPresent(user -> {
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user_details", result);
                redirectAttributes.addFlashAttribute("user_details", userDetail);
            } else {
                userService.updateDetails(user, userDetail);
                redirectAttributes.addFlashAttribute("success", true);
            }
        });

        return "redirect:/profile";
    }

}
