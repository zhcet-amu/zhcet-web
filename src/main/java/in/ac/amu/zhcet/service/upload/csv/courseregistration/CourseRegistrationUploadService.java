package in.ac.amu.zhcet.service.upload.csv.courseregistration;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.dto.upload.RegistrationUpload;
import in.ac.amu.zhcet.service.CourseRegistrationService;
import in.ac.amu.zhcet.service.upload.csv.base.Confirmation;
import in.ac.amu.zhcet.service.upload.csv.base.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.IOException;

@Slf4j
@Service
public class CourseRegistrationUploadService {

    private final CourseRegistrationService courseRegistrationService;
    private final CourseRegistrationUploadAdapter courseRegistrationUploadAdapter;

    @Autowired
    public CourseRegistrationUploadService(CourseRegistrationService courseRegistrationService, CourseRegistrationUploadAdapter courseRegistrationUploadAdapter) {
        this.courseRegistrationService = courseRegistrationService;
        this.courseRegistrationUploadAdapter = courseRegistrationUploadAdapter;
    }

    public void upload(Course course, MultipartFile file, RedirectAttributes attributes, HttpSession session) {
        try {
            UploadResult<RegistrationUpload> result = handleUpload(file);

            if (!result.getErrors().isEmpty()) {
                attributes.addFlashAttribute("errors", result.getErrors());
            } else {
                attributes.addFlashAttribute("success", true);
                Confirmation<CourseRegistration> confirmation = confirmUpload(course, result);
                session.setAttribute("confirmRegistration", confirmation);
            }
        } catch (IOException ioe) {
            log.error("Error registering students", ioe);
        }
    }

    public void register(Course course, RedirectAttributes attributes, HttpSession session) {
        try {
            registerStudents(course, (Confirmation<CourseRegistration>) session.getAttribute("confirmRegistration"));
            attributes.addFlashAttribute("registered", true);
        } catch (Exception e) {
            log.error("Error confirming student registrations", e);
            attributes.addFlashAttribute("unknown_error", true);
        }
    }

    private UploadResult<RegistrationUpload> handleUpload(MultipartFile file) throws IOException {
        return courseRegistrationUploadAdapter.fileToUpload(file);
    }

    private Confirmation<CourseRegistration> confirmUpload(Course course, UploadResult<RegistrationUpload> uploadResult) {
        return courseRegistrationUploadAdapter.uploadToConfirmation(course, uploadResult);
    }

    @Transactional
    private void registerStudents(Course course, Confirmation<CourseRegistration> confirmation) {
        courseRegistrationService.registerStudents(course, confirmation.getData());
    }

}
