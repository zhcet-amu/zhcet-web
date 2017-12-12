package in.ac.amu.zhcet.service.upload.csv.courseregistration;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.dto.upload.RegistrationUpload;
import in.ac.amu.zhcet.service.CourseManagementService;
import in.ac.amu.zhcet.service.StudentService;
import in.ac.amu.zhcet.service.upload.csv.base.AbstractUploadService;
import in.ac.amu.zhcet.service.upload.csv.base.Confirmation;
import in.ac.amu.zhcet.service.upload.csv.base.UploadResult;
import in.ac.amu.zhcet.utils.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CourseRegistrationUploadAdapter {

    private final CourseManagementService courseManagementService;
    private final StudentService studentService;
    private final AbstractUploadService<RegistrationUpload, CourseRegistration> uploadService;

    @Data
    private static class ErrorConditions {
        private boolean invalidEnrolment;
        private boolean alreadyEnrolled;
    }

    @Autowired
    public CourseRegistrationUploadAdapter(CourseManagementService courseManagementService, StudentService studentService, AbstractUploadService<RegistrationUpload, CourseRegistration> uploadService) {
        this.courseManagementService = courseManagementService;
        this.studentService = studentService;
        this.uploadService = uploadService;
    }

    UploadResult<RegistrationUpload> fileToUpload(MultipartFile file) throws IOException {
        return uploadService.handleUpload(RegistrationUpload.class, file);
    }

    Confirmation<CourseRegistration> uploadToConfirmation(Course course, UploadResult<RegistrationUpload> uploadResult) {
        ErrorConditions conditions = new ErrorConditions();

        Optional<Confirmation<CourseRegistration>> registrationConfirmationOptional = courseManagementService.getFloatedCourse(course)
                .flatMap(floatedCourse -> Optional.of(floatedCourse.getCourseRegistrations()))
                .flatMap(registrations -> Optional.of(uploadService.confirmUpload(
                        uploadResult,
                        this::fromRegistrationUpload,
                        courseRegistration -> getMappedValue(courseRegistration.getStudent(), course, registrations, conditions)
                )));

        registrationConfirmationOptional.ifPresent(registrationConfirmation -> {
            if (conditions.isInvalidEnrolment())
                registrationConfirmation.getErrors().add("Invalid student faculty number found");
            if (conditions.isAlreadyEnrolled())
                registrationConfirmation.getErrors().add("Students already enrolled in course found");

            if (!registrationConfirmation.getErrors().isEmpty()) {
                log.warn(registrationConfirmation.getErrors().toString());
            }
        });

        return registrationConfirmationOptional.orElseGet(null);
    }

    private CourseRegistration fromRegistrationUpload(RegistrationUpload upload) {
        Student student = studentService.getByFacultyNumber(StringUtils.capitalizeAll(upload.getFacultyNo()))
                .orElseGet(() -> Student.builder().facultyNumber(upload.getFacultyNo()).build());

        CourseRegistration courseRegistration = new CourseRegistration();
        courseRegistration.setMode(upload.getMode());
        courseRegistration.setStudent(student);

        return courseRegistration;
    }

    private String getMappedValue(Student student, Course course, List<CourseRegistration> registrations, ErrorConditions conditions) {
        if (student.getEnrolmentNumber() == null) {
            conditions.setInvalidEnrolment(true);
            log.warn("Course Registration : Invalid Faculty Number {} {}", course.getCode(), student.getFacultyNumber());
            return  "No such student found";
        } else if(registrations.stream()
                .map(CourseRegistration::getStudent)
                .anyMatch(oldStudent -> oldStudent.equals(student))) {
            conditions.setAlreadyEnrolled(true);
            log.warn("Student already enrolled in course : {} {}", course.getCode(), student.getEnrolmentNumber());
            return "Already enrolled in " + course.getCode();
        } else {
            return null;
        }
    }

}
