package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.base.user.UserAuth;
import in.ac.amu.zhcet.data.repository.DepartmentRepository;
import in.ac.amu.zhcet.data.service.FacultyService;
import in.ac.amu.zhcet.data.service.StudentService;
import in.ac.amu.zhcet.data.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Slf4j
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
        if (!model.containsAttribute("user")) {
            UserAuth user = new UserAuth();
            user.setType("STUDENT");

            model.addAttribute("user", user);
        }

        model.addAttribute("users", userService.getAll());

        if (!model.containsAttribute("department")) {
            model.addAttribute("department", new Department());
        }

        model.addAttribute("departments", departmentRepository.findAll());

        return "dean";
    }

    @PostMapping("/dean")
    public String enterUser(@Valid UserAuth user, BindingResult bindingResult, @RequestParam long department, @RequestParam String roles, RedirectAttributes redirectAttributes) {
        user.setRoles(roles.split(","));

        redirectAttributes.addFlashAttribute("user", user);
        if (bindingResult.hasErrors()) {
            log.error(bindingResult.toString());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", bindingResult);
        } else {
            final List<String> errors = new ArrayList<>();

            Consumer<Throwable> errorHandler = throwable -> {
                if (throwable instanceof DataIntegrityViolationException && throwable.getMessage().contains("PRIMARY_KEY"))
                    errors.add("User with this ID already exists");
            };

            if (user.getType().equals("STUDENT")) {
                Student student = new Student(user, null);
                student.getUserDetails().setDepartment(departmentRepository.findOne(department));

                errors.addAll(validate(student));
                saveAction(errors, redirectAttributes, () -> studentService.register(student), errorHandler);
            } else {
                FacultyMember facultyMember = new FacultyMember(user);
                facultyMember.getUserDetails().setDepartment(departmentRepository.findOne(department));

                errors.addAll(validate(facultyMember));
                saveAction(errors, redirectAttributes, () -> facultyService.register(facultyMember), errorHandler);
            }

            redirectAttributes.addFlashAttribute("user_errors", errors);
        }

        return "redirect:dean";
    }

    @PostMapping("/dean/add_department")
    public String addDepartment(@Valid Department department, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.department", bindingResult);
            redirectAttributes.addFlashAttribute("department", department);
        } else {
            try {
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

    private static void saveAction(List<String> errors, RedirectAttributes redirectAttributes, Runnable action, Consumer<Throwable> throwableConsumer) {
        if (errors.isEmpty()) {
            try {
                action.run();
                redirectAttributes.addFlashAttribute("user_success", true);
            } catch (Exception e) {
                e.printStackTrace();

                if (throwableConsumer != null)
                    throwableConsumer.accept(e);
            }
        }
    }

    private static <T> List<String> validate(T object) {
        List<String> errors = new ArrayList<>();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> constraints = validator.validate(object);
        for (ConstraintViolation<T> constraint : constraints) {
            errors.add(constraint.getPropertyPath() + "  " + constraint.getMessage());
        }

        return errors;
    }
}
