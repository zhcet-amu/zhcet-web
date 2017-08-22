package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.base.user.Type;
import in.ac.amu.zhcet.data.model.base.user.UserAuth;
import in.ac.amu.zhcet.data.model.base.user.UserDetail;
import in.ac.amu.zhcet.data.service.FacultyService;
import in.ac.amu.zhcet.data.service.StudentService;
import in.ac.amu.zhcet.data.service.UserDetailService;
import in.ac.amu.zhcet.data.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class ProfileController {

    private final UserService userService;
    private final StudentService studentService;
    private final FacultyService facultyService;
    private final UserDetailService userDetailService;

    @Autowired
    public ProfileController(UserService userService, StudentService studentService, FacultyService facultyService, UserDetailService userDetailService) {
        this.userService = userService;
        this.studentService = studentService;
        this.facultyService = facultyService;
        this.userDetailService = userDetailService;
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
