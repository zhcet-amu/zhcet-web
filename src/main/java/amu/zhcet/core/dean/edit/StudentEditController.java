package amu.zhcet.core.dean.edit;

import amu.zhcet.core.error.ErrorUtils;
import amu.zhcet.data.department.DepartmentService;
import amu.zhcet.data.user.Gender;
import amu.zhcet.data.user.student.HallCode;
import amu.zhcet.data.user.student.Student;
import amu.zhcet.data.user.student.StudentStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Collections;

@Slf4j
@Controller
@RequestMapping("/admin/dean/students")
public class StudentEditController {

    private final StudentEditService studentEditService;
    private final DepartmentService departmentService;

    public StudentEditController(StudentEditService studentEditService, DepartmentService departmentService) {
        this.studentEditService = studentEditService;
        this.departmentService = departmentService;
    }

    @GetMapping
    public String students(Model model) {
        model.addAttribute("page_title", "Student Manager");
        model.addAttribute("page_subtitle", "Registered Student Management");
        model.addAttribute("page_description", "Search and manage registered students and edit details");
        return "dean/students_page";
    }

    @GetMapping("{student}")
    public String student(Model model, @PathVariable Student student) {
        ErrorUtils.requireNonNullStudent(student);

        model.addAttribute("page_title", "Student Editor");
        model.addAttribute("page_description", "Change student specific details");
        model.addAttribute("student", student);
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("hallCodes", EnumUtils.getEnumMap(HallCode.class).keySet());
        model.addAttribute("statuses", EnumUtils.getEnumMap(StudentStatus.class).keySet());
        model.addAttribute("genders", Gender.values());
        if (!model.containsAttribute("studentModel")) {
            model.addAttribute("page_subtitle", "Edit details of " + student.getUser().getName());
            model.addAttribute("studentModel", studentEditService.fromStudent(student));
        }

        return "dean/student_edit";
    }

    @PostMapping("{student}")
    public String studentPost(RedirectAttributes redirectAttributes, @PathVariable Student student, @Valid StudentEditModel studentEditModel, BindingResult result) {
        ErrorUtils.requireNonNullStudent(student);

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.studentModel", result);
            redirectAttributes.addFlashAttribute("studentModel", studentEditModel);
        } else {
            try {
                studentEditService.saveStudent(student, studentEditModel);
                redirectAttributes.addFlashAttribute("success", Collections.singletonList("Student successfully updated"));
            } catch (RuntimeException re) {
                log.warn("Error saving student", re);

                redirectAttributes.addFlashAttribute("errors", Collections.singletonList(re.getMessage()));
                redirectAttributes.addFlashAttribute("studentModel", studentEditModel);
            }
        }

        return "redirect:/admin/dean/students/{student}";
    }

}
