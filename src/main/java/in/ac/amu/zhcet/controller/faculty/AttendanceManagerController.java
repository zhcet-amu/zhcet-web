package in.ac.amu.zhcet.controller.faculty;

import in.ac.amu.zhcet.data.model.dto.upload.AttendanceUpload;
import in.ac.amu.zhcet.service.CourseInChargeService;
import in.ac.amu.zhcet.service.upload.csv.attendance.AttendanceUploadService;
import in.ac.amu.zhcet.service.upload.csv.Confirmation;
import in.ac.amu.zhcet.service.upload.csv.UploadResult;
import in.ac.amu.zhcet.utils.SortUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class AttendanceManagerController {

    private final CourseInChargeService courseInChargeService;
    private final AttendanceUploadService attendanceUploadService;

    @Autowired
    public AttendanceManagerController(CourseInChargeService courseInChargeService, AttendanceUploadService attendanceUploadService) {
        this.courseInChargeService = courseInChargeService;
        this.attendanceUploadService = attendanceUploadService;
    }

    @Data
    private static class AttendanceModel {
        private List<AttendanceUpload> uploadList;
    }

    @PostMapping("faculty/courses/{code}/attendance/edit")
    public String uploadFile(RedirectAttributes attributes, @PathVariable String code, @RequestParam MultipartFile file) {
        courseInChargeService.getCourseInCharge(code).ifPresent(courseInCharge -> {
            try {
                UploadResult<AttendanceUpload> result = attendanceUploadService.handleUpload(file);

                if (!result.getErrors().isEmpty()) {
                    attributes.addFlashAttribute("errors", result.getErrors());
                } else {
                    attributes.addFlashAttribute("success", true);
                    Confirmation<AttendanceUpload> confirmation = attendanceUploadService.confirmUpload(courseInCharge, result);

                    if (confirmation.getErrors().isEmpty()) {
                        AttendanceModel attendanceModel = new AttendanceModel();
                        List<AttendanceUpload> attendanceUploads = new ArrayList<>();
                        attendanceUploads.addAll(confirmation.getData());
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
        });

        return "redirect:/faculty/courses/{code}/attendance";
    }

    @PostMapping("faculty/courses/{code}/attendance/edit/confirm")
    public String uploadAttendance(RedirectAttributes attributes, @PathVariable String code, @Valid @ModelAttribute AttendanceModel attendanceModel, BindingResult bindingResult) {
        courseInChargeService.getCourseInCharge(code).ifPresent(courseInCharge -> {
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
        });

        return "redirect:/faculty/courses/{code}/attendance";
    }

}
