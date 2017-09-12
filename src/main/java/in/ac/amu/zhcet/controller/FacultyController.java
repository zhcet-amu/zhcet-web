package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import in.ac.amu.zhcet.data.model.dto.AttendanceUpload;
import in.ac.amu.zhcet.service.core.FacultyService;
import in.ac.amu.zhcet.service.core.FloatedCourseService;
import in.ac.amu.zhcet.service.core.upload.AttendanceUploadService;
import in.ac.amu.zhcet.service.core.upload.base.Confirmation;
import in.ac.amu.zhcet.service.core.upload.base.UploadResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class FacultyController {
    private final FacultyService facultyService;
    private final FloatedCourseService floatedCourseService;
    private final AttendanceUploadService attendanceUploadService;

    @Autowired
    public FacultyController(FacultyService facultyService, FloatedCourseService floatedCourseService, AttendanceUploadService attendanceUploadService) {
        this.facultyService = facultyService;
        this.floatedCourseService = floatedCourseService;
        this.attendanceUploadService = attendanceUploadService;
    }

    @GetMapping("/faculty/courses")
    public String getCourse(Model model) {
        model.addAttribute("title", "Course Management");
        model.addAttribute("subtitle", "Faculty Floated Course Management");
        model.addAttribute("description", "Manage and upload attendance for currently floated courses");
        String selected = "";
        FacultyMember facultyMember = facultyService.getLoggedInMember();
        List<FloatedCourse> floatedCourses = floatedCourseService.getByFaculty(facultyMember);
        model.addAttribute("floatedCourses", floatedCourses);
        model.addAttribute("selectedCourse", selected);
        return "faculty_courses";
    }

    @GetMapping("faculty/courses/{id}")
    public String getStudents(Model model, @PathVariable String id) {
        FloatedCourse floatedCourse = floatedCourseService.getCourseById(id);

        if (floatedCourse != null) {
            model.addAttribute("title", floatedCourse.getCourse().getTitle());
            model.addAttribute("subtitle", "Attendance management for " + floatedCourse.getCourse().getCode());
            model.addAttribute("description", "Upload attendance for the floated course");

            List<CourseRegistration> courseRegistrations = floatedCourse.getCourseRegistrations();
            model.addAttribute("courseRegistrations", courseRegistrations);
            model.addAttribute("course_id", id);
        }

        return "course_attendance";
    }

    @Data
    private static class AttendanceModel {
        private List<AttendanceUpload> uploadList;
    }

    @PostMapping("faculty/courses/{id}/attendance")
    public String uploadFile(RedirectAttributes attributes, @PathVariable String id, @RequestParam("file") MultipartFile file) {
        try {
            UploadResult<AttendanceUpload> result = attendanceUploadService.handleUpload(file);

            if (!result.getErrors().isEmpty()) {
                attributes.addFlashAttribute("errors", result.getErrors());
            } else {
                attributes.addFlashAttribute("success", true);
                Confirmation<AttendanceUpload, Boolean> confirmation = attendanceUploadService.confirmUpload(id, result);

                if (confirmation.getErrors().isEmpty()) {
                    AttendanceModel attendanceModel = new AttendanceModel();
                    List<AttendanceUpload> attendanceUploads = new ArrayList<>();
                    attendanceUploads.addAll(confirmation.getData().keySet());
                    attendanceModel.setUploadList(attendanceUploads);
                    attributes.addFlashAttribute("attendanceModel", attendanceModel);
                } else {
                    attributes.addFlashAttribute("confirmAttendanceErrors", confirmation);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return "redirect:/faculty/courses/{id}";
    }

    @PostMapping("faculty/courses/{id}/attendance_confirmed")
    public String uploadAttendance(RedirectAttributes attributes, @PathVariable String id, @Valid @ModelAttribute AttendanceModel attendanceModel, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            attributes.addFlashAttribute("attendanceModel", attendanceModel);
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.attendanceModel", bindingResult);
        } else {
            try {
                attendanceUploadService.updateAttendance(id, attendanceModel.getUploadList());
                attributes.addFlashAttribute("updated", true);
            } catch (Exception e) {
                attributes.addFlashAttribute("attendanceModel", attendanceModel);
                attributes.addFlashAttribute("unknown_error", true);
            }
        }

        return "redirect:/faculty/courses/{id}";
    }
}
