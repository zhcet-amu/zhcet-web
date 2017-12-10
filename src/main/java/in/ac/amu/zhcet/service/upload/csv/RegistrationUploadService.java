package in.ac.amu.zhcet.service.upload.csv;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.dto.upload.RegistrationUpload;
import in.ac.amu.zhcet.service.CourseManagementService;
import in.ac.amu.zhcet.service.CourseRegistrationService;
import in.ac.amu.zhcet.service.StudentService;
import in.ac.amu.zhcet.service.upload.csv.base.AbstractUploadService;
import in.ac.amu.zhcet.service.upload.csv.base.Confirmation;
import in.ac.amu.zhcet.service.upload.csv.base.UploadResult;
import in.ac.amu.zhcet.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class RegistrationUploadService {

    private boolean invalidEnrolment;
    private boolean alreadyEnrolled;

    private final StudentService studentService;
    private final CourseManagementService courseManagementService;
    private final CourseRegistrationService courseRegistrationService;
    private final AbstractUploadService<RegistrationUpload, CourseRegistration> uploadService;

    @Autowired
    public RegistrationUploadService(StudentService studentService, CourseManagementService courseManagementService, CourseRegistrationService courseRegistrationService, AbstractUploadService<RegistrationUpload, CourseRegistration> uploadService) {
        this.studentService = studentService;
        this.courseManagementService = courseManagementService;
        this.courseRegistrationService = courseRegistrationService;
        this.uploadService = uploadService;
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
        return uploadService.handleUpload(RegistrationUpload.class, file);
    }

    private CourseRegistration fromRegistrationUpload(RegistrationUpload upload) {
        Student student = studentService.getByFacultyNumber(StringUtils.capitalizeAll(upload.getFacultyNo()))
                .orElseGet(() -> Student.builder().facultyNumber(upload.getFacultyNo()).build());

        CourseRegistration courseRegistration = new CourseRegistration();
        courseRegistration.setMode(upload.getMode());
        courseRegistration.setStudent(student);

        return courseRegistration;
    }

    private String getMappedValue(Student student, Course course, List<CourseRegistration> registrations) {
        if (student.getEnrolmentNumber() == null) {
            invalidEnrolment = true;
            log.info("Course Registration : Invalid Faculty Number {} {}", course.getCode(), student.getFacultyNumber());
            return  "No such student found";
        } else if(registrations.stream()
                .map(CourseRegistration::getStudent)
                .anyMatch(oldStudent -> oldStudent.equals(student))) {
            alreadyEnrolled = true;
            log.info("Student already enrolled in course : {} {}", course.getCode(), student.getEnrolmentNumber());
            return "Already enrolled in " + course.getCode();
        } else {
            return null;
        }
    }

    private Confirmation<CourseRegistration> confirmUpload(Course course, UploadResult<RegistrationUpload> uploadResult) {
        invalidEnrolment = false;
        alreadyEnrolled = false;

        Optional<Confirmation<CourseRegistration>> registrationConfirmationOptional = courseManagementService.getFloatedCourse(course)
                .flatMap(floatedCourse -> Optional.of(floatedCourse.getCourseRegistrations()))
                .flatMap(registrations -> Optional.of(uploadService.confirmUpload(
                        uploadResult,
                        this::fromRegistrationUpload,
                        courseRegistration -> getMappedValue(courseRegistration.getStudent(), course, registrations)
                )));

        registrationConfirmationOptional.ifPresent(registrationConfirmation -> {
            if (invalidEnrolment)
                registrationConfirmation.getErrors().add("Invalid student faculty number found");
            if (alreadyEnrolled)
                registrationConfirmation.getErrors().add("Students already enrolled in course found");

            if (!registrationConfirmation.getErrors().isEmpty()) {
                log.warn(registrationConfirmation.getErrors().toString());
            }
        });

        return registrationConfirmationOptional.orElseGet(null);
    }

    @Transactional
    private void registerStudents(Course course, Confirmation<CourseRegistration> confirmation) {
        courseRegistrationService.registerStudents(course, confirmation.getData());
    }

}
