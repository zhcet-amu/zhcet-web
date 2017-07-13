package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.Roles;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.base.user.UserAuth;
import in.ac.amu.zhcet.data.repository.DepartmentRepository;
import in.ac.amu.zhcet.data.service.FacultyService;
import in.ac.amu.zhcet.data.service.StudentService;
import in.ac.amu.zhcet.data.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DeanController {

    private final UserService userService;
    private final DepartmentRepository departmentRepository;
    private final StudentService studentService;
    private final FacultyService facultyService;

    @Autowired
    public DeanController(UserService userService, StudentService studentService, FacultyService facultyService, DepartmentRepository departmentRepository) {
        this.userService = userService;
        this.studentService = studentService;
        this.facultyService = facultyService;
        this.departmentRepository = departmentRepository;
    }

    @GetMapping("/dean")
    public String deanAdmin(Model model) {
        UserAuth user = new UserAuth();
        user.setType("STUDENT");
        model.addAttribute("user", user);
        model.addAttribute("users", userService.getAll());
        model.addAttribute("departments", departmentRepository.findAll());

        return "dean";
    }

    @PostMapping("/dean")
    public String enterUser(@ModelAttribute UserAuth user, @RequestParam long department) {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_" + user.getType());

        user.setRoles(roles.toArray(new String[roles.size()]));

        if (user.getType().equals("STUDENT")) {
            Student student = new Student(user, null);
            student.getUserDetails().setDepartment(departmentRepository.findOne(department));
            studentService.register(student);
        } else {
            user.setType("FACULTY");
            if (!roles.contains(Roles.FACULTY))
                roles.add(Roles.FACULTY);
            user.setRoles(roles.toArray(new String[roles.size()]));
            FacultyMember facultyMember = new FacultyMember(user);
            facultyMember.getUserDetails().setDepartment(departmentRepository.findOne(department));
            facultyService.register(facultyMember);
        }

        return "redirect:dean";
    }
}
