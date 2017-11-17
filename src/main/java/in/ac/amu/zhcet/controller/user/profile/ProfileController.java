package in.ac.amu.zhcet.controller.user.profile;

import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.user.Type;
import in.ac.amu.zhcet.data.model.user.User;
import in.ac.amu.zhcet.data.model.user.UserDetail;
import in.ac.amu.zhcet.data.type.Gender;
import in.ac.amu.zhcet.service.FacultyService;
import in.ac.amu.zhcet.service.StudentService;
import in.ac.amu.zhcet.service.UserService;
import in.ac.amu.zhcet.service.user.UserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        User user = userService.getLoggedInUser();
        model.addAttribute("user", user);

        if (!model.containsAttribute("user_details"))
            model.addAttribute("user_details", user.getDetails());

        model.addAttribute("page_title", "Profile");
        model.addAttribute("page_subtitle", "Profile Settings for " + user.getName());
        model.addAttribute("page_description", "Manage Profile Details and Information");
        model.addAttribute("genders", Gender.values());

        if (user.getType().equals(Type.STUDENT)) {
            Student student = studentService.getLoggedInStudent();
            model.addAttribute("student", student);
        } else {
            FacultyMember facultyMember = facultyService.getLoggedInMember();
            model.addAttribute("faculty", facultyMember);
        }

        if (request.getParameterMap().containsKey("refresh"))
            userDetailService.updatePrincipal(user);

        return "user/profile";
    }

    @PostMapping("/profile/details")
    public String saveProfile(@ModelAttribute @Valid UserDetail userDetail, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user_details", result);
            redirectAttributes.addFlashAttribute("user_details", userDetail);
        } else {
            userService.updateDetails(userService.getLoggedInUser(), userDetail);
            redirectAttributes.addFlashAttribute("success", true);
        }

        return "redirect:/profile";
    }

    @GetMapping("/profile/email")
    public String unsubscribeEmail(@RequestParam(required = false) Boolean unsubscribe) {
        if (unsubscribe == null)
            return "redirect:/profile/email?unsubscribe=false";

        userService.unsubscribeEmail(userService.getLoggedInUser(), unsubscribe);

        return "redirect:/profile";
    }

}
