package in.ac.amu.zhcet.controller.dean.datatables;

import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.dto.datatables.FacultyEditModel;
import in.ac.amu.zhcet.data.type.Gender;
import in.ac.amu.zhcet.service.DepartmentService;
import in.ac.amu.zhcet.service.FacultyEditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Optional;

@Slf4j
@Controller
public class FacultyEditController {

    private final DepartmentService departmentService;
    private final FacultyEditService facultyEditService;

    @Autowired
    public FacultyEditController(DepartmentService departmentService, FacultyEditService facultyEditService) {
        this.departmentService = departmentService;
        this.facultyEditService = facultyEditService;
    }

    @GetMapping("/dean/faculty")
    public String students(Model model) {
        model.addAttribute("page_title", "Faculty Manager");
        model.addAttribute("page_subtitle", "Registered Faculty Management");
        model.addAttribute("page_description", "Search and manage registered faculty and edit details");
        return "dean/faculty_page";
    }

    @GetMapping("/dean/faculty/{id}")
    public String student(Model model, @PathVariable("id") FacultyMember faculty) {
        Optional.ofNullable(faculty).ifPresent(facultyMember -> {
            model.addAttribute("page_title", "Faculty Editor");
            model.addAttribute("page_description", "Change faculty specific details");
            model.addAttribute("faculty", facultyMember);
            model.addAttribute("departments", departmentService.findAll());
            model.addAttribute("genders", Gender.values());
            if (!model.containsAttribute("facultyModel")) {
                model.addAttribute("page_subtitle", "Edit details of " + facultyMember.getUser().getName());
                model.addAttribute("facultyModel", facultyEditService.fromFaculty(facultyMember));
            }
        });

        return "dean/faculty_edit";
    }

    @PostMapping("/dean/faculty/{id}")
    public String studentPost(RedirectAttributes redirectAttributes, @PathVariable("id") FacultyMember faculty, @Valid FacultyEditModel facultyEditModel, BindingResult result) {
        Optional.ofNullable(faculty).ifPresent(facultyMember -> {
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.facultyModel", result);
                redirectAttributes.addFlashAttribute("facultyModel", facultyEditModel);
            } else {
                List<String> errors = new ArrayList<>();

                try {
                    facultyEditService.saveFacultyMember(facultyMember.getFacultyId(), facultyEditModel);
                    redirectAttributes.addFlashAttribute("success", Collections.singletonList("Faculty successfully updated"));
                } catch (RuntimeException re) {
                    log.error("Error saving faculty", re);
                    errors.add(re.getMessage());
                    redirectAttributes.addFlashAttribute("errors", errors);
                    redirectAttributes.addFlashAttribute("facultyModel", facultyEditModel);
                }
            }
        });

        return "redirect:/dean/faculty/{id}";
    }

}
