package in.ac.amu.zhcet.controller.faculty;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.dto.upload.AttendanceUpload;
import in.ac.amu.zhcet.service.csv.AttendanceUploadService;
import in.ac.amu.zhcet.service.csv.base.Confirmation;
import in.ac.amu.zhcet.service.csv.base.UploadResult;
import in.ac.amu.zhcet.service.misc.EmailNotificationService;
import in.ac.amu.zhcet.utils.Utils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    private final AttendanceUploadService attendanceUploadService;
    private final EmailNotificationService emailNotificationService;

    @Autowired
    public AttendanceManagerController(AttendanceUploadService attendanceUploadService, EmailNotificationService emailNotificationService) {
        this.attendanceUploadService = attendanceUploadService;
        this.emailNotificationService = emailNotificationService;
    }

    @Data
    private static class AttendanceModel {
        private List<AttendanceUpload> uploadList;
    }

    @PostMapping("faculty/courses/{course}/attendance/edit")
    public String uploadFile(RedirectAttributes attributes, @PathVariable Course course, @RequestParam(required = false) String section, @RequestParam MultipartFile file) {
        try {
            UploadResult<AttendanceUpload> result = attendanceUploadService.handleUpload(file);

            if (!result.getErrors().isEmpty()) {
                attributes.addFlashAttribute("errors", result.getErrors());
            } else {
                attributes.addFlashAttribute("success", true);
                Confirmation<AttendanceUpload> confirmation = attendanceUploadService.confirmUpload(course, section, result);

                if (confirmation.getErrors().isEmpty()) {
                    AttendanceModel attendanceModel = new AttendanceModel();
                    List<AttendanceUpload> attendanceUploads = new ArrayList<>();
                    attendanceUploads.addAll(confirmation.getData());
                    Utils.sortAttendanceUpload(attendanceUploads);
                    attendanceModel.setUploadList(attendanceUploads);
                    attributes.addFlashAttribute("attendanceModel", attendanceModel);
                } else {
                    attributes.addFlashAttribute("confirmAttendanceErrors", confirmation);
                }
            }
        } catch (IOException ioe) {
            log.error("Attendance Upload", ioe);
        }

        return "redirect:/faculty/courses/{course}/attendance?section=" + StringUtils.defaultString(section, "");
    }

    @PostMapping("faculty/courses/{course}/attendance/edit/confirm")
    public String uploadAttendance(RedirectAttributes attributes, @PathVariable Course course, @RequestParam(required = false) String section, @Valid @ModelAttribute AttendanceModel attendanceModel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            attributes.addFlashAttribute("attendanceModel", attendanceModel);
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.attendanceModel", bindingResult);
        } else {
            try {
                attendanceUploadService.updateAttendance(course, section, attendanceModel.getUploadList());
                emailNotificationService.sendNotificationsForAttendance(course, attendanceModel.getUploadList());
                attributes.addFlashAttribute("updated", true);
            } catch (Exception e) {
                log.error("Attendance Confirm", e);
                attributes.addFlashAttribute("attendanceModel", attendanceModel);
                attributes.addFlashAttribute("unknown_error", true);
            }
        }

        return "redirect:/faculty/courses/{course}/attendance?section=" + StringUtils.defaultString(section, "");
    }

}
