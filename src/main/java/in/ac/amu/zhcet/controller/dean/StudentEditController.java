package in.ac.amu.zhcet.controller.dean;

import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.dto.StudentEditModel;
import in.ac.amu.zhcet.service.core.DepartmentService;
import in.ac.amu.zhcet.service.core.StudentEditService;
import in.ac.amu.zhcet.service.core.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
public class StudentEditController {

    private final StudentService studentService;
    private final StudentEditService studentEditService;
    private final DepartmentService departmentService;

    public StudentEditController(StudentService studentService, StudentEditService studentEditService, DepartmentService departmentService) {
        this.studentService = studentService;
        this.studentEditService = studentEditService;
        this.departmentService = departmentService;
    }

    @GetMapping("/dean/students")
    public String students(Model model) {
        model.addAttribute("title", "Student Manager");
        model.addAttribute("subtitle", "Registered Student Management");
        model.addAttribute("description", "Search and manage registered students and edit details");
        return "students_page";
    }

    @GetMapping("/dean/students/{id}")
    public String student(Model model, @PathVariable String id) {
        Student student = studentService.getByEnrolmentNumber(id);
        model.addAttribute("title", "Student Editor");
        model.addAttribute("description", "Change student specific details");
        model.addAttribute("student", student);
        model.addAttribute("departments", departmentService.findAll());
        if (student != null && !model.containsAttribute("studentModel")) {
            model.addAttribute("subtitle", "Edit details of " + student.getUser().getName());
            model.addAttribute("studentModel", studentEditService.fromStudent(student));
        }

        return "student_edit";
    }

    @PostMapping("/dean/students/{id}")
    public String studentPost(RedirectAttributes redirectAttributes, @PathVariable String id, @Valid StudentEditModel studentEditModel, BindingResult result) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.studentModel", result);
            redirectAttributes.addFlashAttribute("studentModel", studentEditModel);
        } else {
            List<String> errors = new ArrayList<>();

            try {
                studentEditService.saveStudent(id, studentEditModel);
                redirectAttributes.addFlashAttribute("success", Collections.singletonList("Student successfully updated"));
            } catch (RuntimeException re) {
                re.printStackTrace();

                errors.add(re.getMessage());
                redirectAttributes.addFlashAttribute("errors", errors);
                redirectAttributes.addFlashAttribute("studentModel", studentEditModel);
            }
        }

        return "redirect:/dean/students/{id}";
    }

}
