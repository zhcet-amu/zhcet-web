package in.ac.amu.zhcet.controller.dean;

import in.ac.amu.zhcet.data.Roles;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.configuration.ConfigurationModel;
import in.ac.amu.zhcet.service.core.ConfigurationService;
import in.ac.amu.zhcet.service.core.DepartmentService;
import in.ac.amu.zhcet.service.core.FacultyService;
import in.ac.amu.zhcet.service.core.UserService;
import in.ac.amu.zhcet.utils.DuplicateException;
import in.ac.amu.zhcet.utils.Utils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Slf4j
@Controller
public class DeanController {

    private final UserService userService;
    private final DepartmentService departmentService;
    private final FacultyService facultyService;
    private final ConfigurationService configurationService;

    @Autowired
    public DeanController(UserService userService, FacultyService facultyService, DepartmentService departmentService, ConfigurationService configurationService) {
        this.userService = userService;
        this.departmentService = departmentService;
        this.facultyService = facultyService;
        this.configurationService = configurationService;
    }

    @GetMapping("/dean")
    public String deanAdmin(Model model) {
        model.addAttribute("title", "Administration Panel");
        model.addAttribute("subtitle", "Dean Administration Panel");
        model.addAttribute("description", "Register Students and Faculty, manage roles and users");


        model.addAttribute("users", userService.getAll());
        if (!model.containsAttribute("department"))
            model.addAttribute("department", new Department());
        model.addAttribute("departments", departmentService.findAll());

        return "dean";
    }

    @GetMapping("/dean/roles/{id}")
    public String roleManagement(Model model, @PathVariable long id) {
        Department department = departmentService.findOne(id);

        if (department != null) {
            model.addAttribute("title", "Role Management");
            model.addAttribute("subtitle", "Role Management Panel for " + department.getName() + " Department");
            model.addAttribute("description", "Manage Faculty Roles and Permissions");

            model.addAttribute("department", department);
            model.addAttribute("facultyMembers", facultyService.getByDepartment(department));
        }

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
                departmentService.addDepartment(department);
                redirectAttributes.addFlashAttribute("dept_success", true);
            } catch (DuplicateException de) {
                List<String> errors = new ArrayList<>();
                errors.add(de.getMessage());

                redirectAttributes.addFlashAttribute("department", department);
                redirectAttributes.addFlashAttribute("dept_errors", errors);
            }
        }

        return "redirect:/dean";
    }

    @Data
    private static class Config {
        private int threshold;
        @NotNull
        private char term;
        private int year;
        private boolean automatic;
        private final String defaultSession = Utils.getDefaultSessionName();
    }

    private Config toConfig(ConfigurationModel configurationModel) {
        Config config = new Config();
        config.setThreshold(configurationModel.getAttendanceThreshold());
        config.setTerm(configurationModel.getSession().charAt(0));
        config.setYear(2000 + Integer.parseInt(configurationModel.getSession().substring(1)));
        config.setAutomatic(configurationModel.isAutomatic());

        return config;
    }

    private ConfigurationModel toConfigModel(Config config) {
        ConfigurationModel configurationModel = new ConfigurationModel();
        configurationModel.setAttendanceThreshold(config.getThreshold());
        configurationModel.setSession(config.getTerm() + String.valueOf(config.getYear() - 2000));
        configurationModel.setAutomatic(config.isAutomatic());

        return configurationModel;
    }

    @GetMapping("/dean/configuration")
    public String configuration(Model model) {
        model.addAttribute("title", "Configuration");
        model.addAttribute("subtitle", "Configuration Management Panel");
        model.addAttribute("description", "Manage site wide configurations and parameters");

        log.info(configurationService.getConfig().toString());
        log.info(configurationService.getSessionName());
        log.info(configurationService.getSession());
        log.info(configurationService.getThreshold() + "");

        if (!model.containsAttribute("config"))
            model.addAttribute("config", toConfig(configurationService.getConfig()));
        return "configuration";
    }

    @PostMapping("/dean/configuration")
    public String configurationPost(RedirectAttributes redirectAttributes, @Valid Config config, BindingResult result) {
        String redirectUrl = "redirect:/dean/configuration";

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("config", config);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.config", result);
            return redirectUrl;
        }

        List<String> errors = new ArrayList<>();
        if (config.getThreshold() < 50 || config.getThreshold() > 100)
            errors.add("Threshold should be 50% and 100%");
        if (config.getYear() < 2000)
            errors.add("Year should be greater than 2000");
        if (config.getTerm() != 'A' && config.getTerm() != 'W')
            errors.add("Term can only be Autumn or Winter");

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("config", config);
            redirectAttributes.addFlashAttribute("errors", errors);
            return redirectUrl;
        } else {
            configurationService.save(toConfigModel(config));
            redirectAttributes.addFlashAttribute("success", Collections.singletonList("Configuration successfully saved!"));
        }

        return redirectUrl;
    }

}
