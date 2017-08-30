package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.Roles;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.repository.DepartmentRepository;
import in.ac.amu.zhcet.data.service.FacultyService;
import in.ac.amu.zhcet.data.service.UserService;
import in.ac.amu.zhcet.data.service.upload.StudentUploadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
public class DeanController {

    private final UserService userService;
    private final DepartmentRepository departmentRepository;
    private final StudentUploadService studentUploadService;
    private final FacultyService facultyService;

    @Autowired
    public DeanController(UserService userService, DepartmentRepository departmentRepository, StudentUploadService studentUploadService, FacultyService facultyService) {
        this.userService = userService;
        this.departmentRepository = departmentRepository;
        this.studentUploadService = studentUploadService;
        this.facultyService = facultyService;
    }

    @GetMapping("/dean")
    public String deanAdmin(Model model) {
        model.addAttribute("users", userService.getAll());

        if (!model.containsAttribute("department")) {
            model.addAttribute("department", new Department());
        }

        model.addAttribute("departments", departmentRepository.findAll());

        return "dean";
    }

    @GetMapping("/dean/roles/{id}")
    public String roleManagement(Model model, @PathVariable long id) {
        Department department = departmentRepository.findOne(id);

        model.addAttribute("department", department);
        model.addAttribute("facultyMembers", facultyService.getByDepartment(department));

        return "role_management";
    }

    @PostMapping("/dean/roles/{id}/save")
    public String saveRoles(Model model, @PathVariable long id, RedirectAttributes redirectAttributes, @RequestParam String facultyId, @RequestParam List<String> roles) {
        FacultyMember facultyMember = facultyService.getById(facultyId);

        List<String> newRoles = new ArrayList<>();

        for (String role : roles) {
            switch (role) {
                case "dean":
                    newRoles.add(Roles.DEAN_ADMIN);
                    break;
                case "department":
                    newRoles.add(Roles.DEPARTMENT_ADMIN);
                    break;
                case "faculty":
                    newRoles.add(Roles.FACULTY);
                    break;
                default:
                    // Skip
            }
        }

        facultyMember.getUser().setRoles(newRoles.toArray(new String[newRoles.size()]));
        facultyService.save(facultyMember);

        redirectAttributes.addFlashAttribute("saved", true);

        return "redirect:/dean/roles/{id}";
    }

    @PostMapping("/dean/add_department")
    public String addDepartment(@Valid Department department, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.department", bindingResult);
            redirectAttributes.addFlashAttribute("department", department);
        } else {
            try {
                department.setName(WordUtils.capitalizeFully(department.getName().trim()));
                departmentRepository.save(department);
                redirectAttributes.addFlashAttribute("dept_success", true);
            } catch (Exception e) {
                List<String> errors = new ArrayList<>();
                if (e.getMessage().contains("UK_DEPT_NAME"))
                    errors.add("This department already exists");

                redirectAttributes.addFlashAttribute("department", department);
                redirectAttributes.addFlashAttribute("dept_errors", errors);
            }
        }

        return "redirect:/dean";
    }

    @PostMapping("/dean/register_students")
    public String uploadFile(RedirectAttributes attributes, @RequestParam("file") MultipartFile file, HttpSession session, WebRequest webRequest) {
        try {
            StudentUploadService.UploadResult result = studentUploadService.handleUpload(file);

            if (!result.getErrors().isEmpty()) {
                webRequest.removeAttribute("confirmStudentRegistration", RequestAttributes.SCOPE_SESSION);
                attributes.addFlashAttribute("students_errors", result.getErrors());
            } else {
                attributes.addFlashAttribute("students_success", true);
                StudentUploadService.StudentConfirmation confirmation = studentUploadService.confirmUpload(result);

                session.setAttribute("confirmStudentRegistration", confirmation);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return "redirect:/dean";
    }

    @PostMapping("/dean/register_students_confirmed")
    public String uploadStudents(RedirectAttributes attributes, HttpSession session, WebRequest webRequest) {
        StudentUploadService.StudentConfirmation confirmation = (StudentUploadService.StudentConfirmation) session.getAttribute("confirmStudentRegistration");

        if (confirmation == null || !confirmation.getErrors().isEmpty()) {
            attributes.addFlashAttribute("errors", Collections.singletonList("Unknown Error"));
        } else {
            studentUploadService.registerStudents(confirmation);
            webRequest.removeAttribute("confirmStudentRegistration", RequestAttributes.SCOPE_SESSION);
            attributes.addFlashAttribute("students_registered", true);
        }

        return "redirect:/dean";
    }

    @PostMapping("/dean/clear_session_students")
    public String clearStudentsRegistrationSession(WebRequest webRequest) {
        webRequest.removeAttribute("confirmStudentRegistration", RequestAttributes.SCOPE_SESSION);

        return "redirect:/dean";
    }
}
