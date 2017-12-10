package in.ac.amu.zhcet.controller.dean.datatables;

import com.google.common.base.Strings;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.dto.datatables.StudentEditModel;
import in.ac.amu.zhcet.data.type.Gender;
import in.ac.amu.zhcet.data.type.HallCode;
import in.ac.amu.zhcet.data.type.StudentStatus;
import in.ac.amu.zhcet.service.DepartmentService;
import in.ac.amu.zhcet.service.StudentEditService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
public class StudentEditController {

    private final StudentEditService studentEditService;
    private final DepartmentService departmentService;

    public StudentEditController(StudentEditService studentEditService, DepartmentService departmentService) {
        this.studentEditService = studentEditService;
        this.departmentService = departmentService;
    }

    @GetMapping("/dean/students")
    public String students(Model model) {
        model.addAttribute("page_title", "Student Manager");
        model.addAttribute("page_subtitle", "Registered Student Management");
        model.addAttribute("page_description", "Search and manage registered students and edit details");
        return "dean/students_page";
    }

    @GetMapping("/dean/students/{id}")
    public String student(Model model, @PathVariable("id") Student studentModel) {
        Optional.ofNullable(studentModel).ifPresent(student -> {
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
        });

        return "dean/student_edit";
    }

    @PostMapping("/dean/students/{id}")
    public String studentPost(RedirectAttributes redirectAttributes, @PathVariable("id") Student studentModel, @Valid StudentEditModel studentEditModel, BindingResult result) {
        Optional.ofNullable(studentModel).ifPresent(student -> {
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.studentModel", result);
                redirectAttributes.addFlashAttribute("studentModel", studentEditModel);
            } else {
                List<String> errors = new ArrayList<>();

                try {
                    studentEditService.saveStudent(student.getEnrolmentNumber(), studentEditModel);
                    redirectAttributes.addFlashAttribute("success", Collections.singletonList("Student successfully updated"));
                } catch (RuntimeException re) {
                    log.error("Error saving student", re);

                    errors.add(re.getMessage());
                    redirectAttributes.addFlashAttribute("errors", errors);
                    redirectAttributes.addFlashAttribute("studentModel", studentEditModel);
                }
            }
        });

        return "redirect:/dean/students/{id}";
    }

    @PostMapping("/dean/student/section") // We use 'student' instead of 'students' so that it does not clash with 'studentPost' method above
    public String studentSection(RedirectAttributes redirectAttributes, @RequestParam List<String> enrolments, @RequestParam String section) {
        if (Strings.isNullOrEmpty(section)) {
            redirectAttributes.addFlashAttribute("section_error", "Section must not be empty");
            return "redirect:/dean/students";
        }

        try {
            studentEditService.changeSections(enrolments, section);
            redirectAttributes.addFlashAttribute("section_success", "Sections changed successfully");
        } catch (Exception e) {
            log.error("Error changing sections", e);
            redirectAttributes.addFlashAttribute("section_error", "Unknown error while changing sections");
        }

        return "redirect:/dean/students";
    }

    @PostMapping("/dean/student/status") // We use 'student' instead of 'students' so that it does not clash with 'studentPost' method above
    public String studentStatus(RedirectAttributes redirectAttributes, @RequestParam List<String> enrolments, @RequestParam String status) {
        if (Strings.isNullOrEmpty(status)) {
            redirectAttributes.addFlashAttribute("section_error", "Status was unchanged");
            return "redirect:/dean/students";
        }

        try {
            studentEditService.changeStatuses(enrolments, status);
            redirectAttributes.addFlashAttribute("section_success", "Statuses changed successfully");
        } catch (Exception e) {
            log.error("Error changing statuses", e);
            redirectAttributes.addFlashAttribute("section_error", "Unknown error while changing statuses");
        }

        return "redirect:/dean/students";
    }

}
