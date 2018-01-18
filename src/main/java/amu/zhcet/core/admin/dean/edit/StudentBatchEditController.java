package amu.zhcet.core.admin.dean.edit;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/dean/student")
public class StudentBatchEditController {

    private final StudentEditService studentEditService;

    @Autowired
    public StudentBatchEditController(StudentEditService studentEditService) {
        this.studentEditService = studentEditService;
    }

    @PostMapping("/section") // We use 'student' instead of 'students' so that it does not clash with 'studentPost' method above
    public String studentSection(RedirectAttributes redirectAttributes, @RequestParam List<String> enrolments, @RequestParam String section) {
        if (Strings.isNullOrEmpty(section)) {
            redirectAttributes.addFlashAttribute("section_error", "Section must not be empty");
            return "redirect:/admin/dean/students";
        }

        try {
            studentEditService.changeSections(enrolments, section);
            redirectAttributes.addFlashAttribute("section_success", "Sections changed successfully");
        } catch (Exception e) {
            log.error("Error changing sections", e);
            redirectAttributes.addFlashAttribute("section_error", "Unknown error while changing sections");
        }

        return "redirect:/admin/dean/students";
    }

    @PostMapping("/status") // We use 'student' instead of 'students' so that it does not clash with 'studentPost' method above
    public String studentStatus(RedirectAttributes redirectAttributes, @RequestParam List<String> enrolments, @RequestParam String status) {
        if (Strings.isNullOrEmpty(status)) {
            redirectAttributes.addFlashAttribute("section_error", "Status was unchanged");
            return "redirect:/admin/dean/students";
        }

        try {
            studentEditService.changeStatuses(enrolments, status);
            redirectAttributes.addFlashAttribute("section_success", "Statuses changed successfully");
        } catch (Exception e) {
            log.error("Error changing statuses", e);
            redirectAttributes.addFlashAttribute("section_error", "Unknown error while changing statuses");
        }

        return "redirect:/admin/dean/students";
    }

}
