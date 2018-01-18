package amu.zhcet.core.admin.faculty.attendance.upload;

import amu.zhcet.common.utils.SortUtils;
import amu.zhcet.data.attendance.AttendanceUpload;
import amu.zhcet.data.course.incharge.CourseInCharge;
import amu.zhcet.data.course.incharge.CourseInChargeNotFoundException;
import amu.zhcet.data.course.incharge.CourseInChargeService;
import amu.zhcet.storage.csv.Confirmation;
import amu.zhcet.storage.csv.UploadResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
@RequestMapping("/admin/faculty/courses/{code}/attendance/edit")
public class AttendanceUploadController {

    private final CourseInChargeService courseInChargeService;
    private final AttendanceUploadService attendanceUploadService;

    @Autowired
    public AttendanceUploadController(CourseInChargeService courseInChargeService, AttendanceUploadService attendanceUploadService) {
        this.courseInChargeService = courseInChargeService;
        this.attendanceUploadService = attendanceUploadService;
    }

    @Data
    private static class AttendanceModel {
        private List<AttendanceUpload> uploadList;
    }

    @PostMapping
    public String uploadFile(RedirectAttributes attributes, @PathVariable String code, @RequestParam MultipartFile file) {
        CourseInCharge courseInCharge = courseInChargeService.getCourseInCharge(code).orElseThrow(CourseInChargeNotFoundException::new);

        try {
            UploadResult<AttendanceUpload> result = attendanceUploadService.handleUpload(file);

            if (!result.getErrors().isEmpty()) {
                attributes.addFlashAttribute("errors", result.getErrors());
            } else {
                attributes.addFlashAttribute("success", true);
                Confirmation<AttendanceUpload> confirmation = attendanceUploadService.confirmUpload(courseInCharge, result);

                if (confirmation.getErrors().isEmpty()) {
                    AttendanceModel attendanceModel = new AttendanceModel();
                    List<AttendanceUpload> attendanceUploads = new ArrayList<>(confirmation.getData());
                    SortUtils.sortAttendanceUpload(attendanceUploads);
                    attendanceModel.setUploadList(attendanceUploads);
                    attributes.addFlashAttribute("attendanceModel", attendanceModel);
                } else {
                    attributes.addFlashAttribute("confirmAttendanceErrors", confirmation);
                }
            }
        } catch (IOException ioe) {
            log.error("Attendance Upload", ioe);
        }

        return "redirect:/admin/faculty/courses/{code}/attendance";
    }

    @PostMapping("/confirm")
    public String uploadAttendance(RedirectAttributes attributes, @PathVariable String code, @Valid @ModelAttribute AttendanceModel attendanceModel, BindingResult bindingResult) {
        CourseInCharge courseInCharge = courseInChargeService.getCourseInCharge(code).orElseThrow(CourseInChargeNotFoundException::new);

        if (bindingResult.hasErrors()) {
            attributes.addFlashAttribute("attendanceModel", attendanceModel);
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.attendanceModel", bindingResult);
        } else {
            try {
                attendanceUploadService.updateAttendance(courseInCharge, attendanceModel.getUploadList());
                attributes.addFlashAttribute("updated", true);
            } catch (Exception e) {
                log.error("Attendance Confirm", e);
                attributes.addFlashAttribute("attendanceModel", attendanceModel);
                attributes.addFlashAttribute("unknown_error", true);
            }
        }

        return "redirect:/admin/faculty/courses/{code}/attendance";
    }

}
