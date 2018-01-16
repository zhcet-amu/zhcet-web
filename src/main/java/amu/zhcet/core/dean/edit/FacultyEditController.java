package amu.zhcet.core.dean.edit;

import amu.zhcet.core.error.ErrorUtils;
import amu.zhcet.data.department.DepartmentService;
import amu.zhcet.data.user.Gender;
import amu.zhcet.data.user.faculty.FacultyMember;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/dean/faculty")
public class FacultyEditController {

    private final DepartmentService departmentService;
    private final FacultyEditService facultyEditService;

    @Autowired
    public FacultyEditController(DepartmentService departmentService, FacultyEditService facultyEditService) {
        this.departmentService = departmentService;
        this.facultyEditService = facultyEditService;
    }

    @GetMapping
    public String students(Model model) {
        model.addAttribute("page_title", "Faculty Manager");
        model.addAttribute("page_subtitle", "Registered Faculty Management");
        model.addAttribute("page_description", "Search and manage registered faculty and edit details");
        return "dean/faculty_page";
    }

    @GetMapping("{faculty}")
    public String student(Model model, @PathVariable FacultyMember faculty) {
        ErrorUtils.requireNonNullFacultyMember(faculty);

        model.addAttribute("page_title", "Faculty Editor");
        model.addAttribute("page_description", "Change faculty specific details");
        model.addAttribute("faculty", faculty);
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("genders", Gender.values());
        if (!model.containsAttribute("facultyModel")) {
            model.addAttribute("page_subtitle", "Edit details of " + faculty.getUser().getName());
            model.addAttribute("facultyModel", facultyEditService.fromFaculty(faculty));
        }

        return "dean/faculty_edit";
    }

    @PostMapping("{faculty}")
    public String studentPost(RedirectAttributes redirectAttributes, @PathVariable FacultyMember faculty, @Valid FacultyEditModel facultyEditModel, BindingResult result) {
        ErrorUtils.requireNonNullFacultyMember(faculty);
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.facultyModel", result);
            redirectAttributes.addFlashAttribute("facultyModel", facultyEditModel);
        } else {
            try {
                facultyEditService.saveFacultyMember(faculty, facultyEditModel);
                redirectAttributes.addFlashAttribute("success", Collections.singletonList("Faculty successfully updated"));
            } catch (RuntimeException re) {
                log.warn("Error saving faculty", re);
                
                redirectAttributes.addFlashAttribute("errors", Collections.singletonList(re.getMessage()));
                redirectAttributes.addFlashAttribute("facultyModel", facultyEditModel);
            }
        }

        return "redirect:/dean/faculty/{faculty}";
    }

}
