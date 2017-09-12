package in.ac.amu.zhcet.controller.faculty;

import in.ac.amu.zhcet.data.model.dto.AttendanceUpload;
import in.ac.amu.zhcet.service.core.upload.AttendanceUploadService;
import in.ac.amu.zhcet.service.core.upload.base.Confirmation;
import in.ac.amu.zhcet.service.core.upload.base.UploadResult;
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
public class AttendanceController {

    private final AttendanceUploadService attendanceUploadService;

    @Autowired
    public AttendanceController(AttendanceUploadService attendanceUploadService) {
        this.attendanceUploadService = attendanceUploadService;
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
