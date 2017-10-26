package in.ac.amu.zhcet.service.csv;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.dto.upload.RegistrationUpload;
import in.ac.amu.zhcet.service.CourseManagementService;
import in.ac.amu.zhcet.service.CourseRegistrationService;
import in.ac.amu.zhcet.service.StudentService;
import in.ac.amu.zhcet.service.csv.base.AbstractUploadService;
import in.ac.amu.zhcet.service.csv.base.Confirmation;
import in.ac.amu.zhcet.service.csv.base.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

import static in.ac.amu.zhcet.utils.Utils.capitalizeAll;

@Slf4j
@Service
public class RegistrationUploadService {

    private boolean invalidEnrolment;
    private boolean alreadyEnrolled;

    private final StudentService studentService;
    private final CourseManagementService courseManagementService;
    private final CourseRegistrationService courseRegistrationService;
    private final AbstractUploadService<RegistrationUpload, CourseRegistration, String> uploadService;

    @Autowired
    public RegistrationUploadService(StudentService studentService, CourseManagementService courseManagementService, CourseRegistrationService courseRegistrationService, AbstractUploadService<RegistrationUpload, CourseRegistration, String> uploadService) {
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
                Confirmation<CourseRegistration, String> confirmation = confirmUpload(course, result);
                session.setAttribute("confirmRegistration", confirmation);
            }
        } catch (IOException ioe) {
            log.error("Error registering students", ioe);
        }
    }

    public void register(Course course, RedirectAttributes attributes, HttpSession session) {
        try {
            registerStudents(course, (Confirmation<CourseRegistration, String>) session.getAttribute("confirmRegistration"));
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
        Student student = studentService.getByFacultyNumber(capitalizeAll(upload.getFacultyNo()));

        if (student == null)
            student = Student.builder().facultyNumber(upload.getFacultyNo()).build();

        CourseRegistration courseRegistration = new CourseRegistration();
        courseRegistration.setMode(upload.getMode());
        courseRegistration.setStudent(student);

        return courseRegistration;
    }

    private String getMappedValue(Student student, Course course, List<CourseRegistration> registrations) {
        if (student.getEnrolmentNumber() == null) {
            invalidEnrolment = true;
            log.warn("Course Registration : Invalid Faculty Number {} {}", course.getCode(), student.getFacultyNumber());
            return  "No such student found";
        } else if(registrations.stream()
                .map(CourseRegistration::getStudent)
                .anyMatch(oldStudent -> oldStudent.equals(student))) {
            alreadyEnrolled = true;
            log.warn("Student already enrolled in course : {} {}", course.getCode(), student.getEnrolmentNumber());
            return "Already enrolled in " + course.getCode();
        } else {
            return null;
        }
    }

    private Confirmation<CourseRegistration, String> confirmUpload(Course course, UploadResult<RegistrationUpload> uploadResult) {
        invalidEnrolment = false;
        alreadyEnrolled = false;

        List<CourseRegistration> registrations = courseManagementService.getFloatedCourseByCourse(course).getCourseRegistrations();

        Confirmation<CourseRegistration, String> registrationConfirmation = uploadService.confirmUpload(
                uploadResult,
                this::fromRegistrationUpload,
                courseRegistration -> getMappedValue(courseRegistration.getStudent(), course, registrations)
        );

        if (invalidEnrolment)
            registrationConfirmation.getErrors().add("Invalid student faculty number found");
        if (alreadyEnrolled)
            registrationConfirmation.getErrors().add("Students already enrolled in course found");

        return registrationConfirmation;
    }

    @Transactional
    private void registerStudents(Course course, Confirmation<CourseRegistration, String> confirmation) {
        courseRegistrationService.registerStudents(course, confirmation.getData().keySet());
    }

}
