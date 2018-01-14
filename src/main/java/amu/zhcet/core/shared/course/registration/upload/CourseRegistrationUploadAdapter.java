package amu.zhcet.core.shared.course.registration.upload;

import amu.zhcet.common.utils.StringUtils;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.floated.FloatedCourseService;
import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.course.registration.CourseRegistration;
import amu.zhcet.data.user.student.Student;
import amu.zhcet.data.user.student.StudentService;
import amu.zhcet.storage.csv.AbstractUploadService;
import amu.zhcet.storage.csv.Confirmation;
import amu.zhcet.storage.csv.UploadResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
class CourseRegistrationUploadAdapter {

    private final FloatedCourseService floatedCourseService;
    private final StudentService studentService;
    private final AbstractUploadService<RegistrationUpload, CourseRegistration> uploadService;

    @Data
    private static class ErrorConditions {
        private boolean invalidEnrolment;
        private boolean alreadyEnrolled;
    }

    @Autowired
    public CourseRegistrationUploadAdapter(FloatedCourseService floatedCourseService, StudentService studentService, AbstractUploadService<RegistrationUpload, CourseRegistration> uploadService) {
        this.floatedCourseService = floatedCourseService;
        this.studentService = studentService;
        this.uploadService = uploadService;
    }

    UploadResult<RegistrationUpload> fileToUpload(MultipartFile file) throws IOException {
        return uploadService.handleUpload(RegistrationUpload.class, file);
    }

    Confirmation<CourseRegistration> uploadToConfirmation(Course course, UploadResult<RegistrationUpload> uploadResult) {
        ErrorConditions conditions = new ErrorConditions();

        return floatedCourseService.getFloatedCourse(course)
                .map(FloatedCourse::getCourseRegistrations)
                .map(registrations -> uploadService.confirmUpload(uploadResult)
                        .convert(this::fromRegistrationUpload)
                        .map(courseRegistration -> getMappedValue(courseRegistration.getStudent(), course, registrations, conditions))
                        .get()
                ).map(registrationConfirmation -> {
                    if (conditions.isInvalidEnrolment())
                        registrationConfirmation.getErrors().add("Invalid student faculty number found");
                    if (conditions.isAlreadyEnrolled())
                        registrationConfirmation.getErrors().add("Students already enrolled in course found");

                    if (!registrationConfirmation.getErrors().isEmpty()) {
                        log.warn(registrationConfirmation.getErrors().toString());
                    }

                    return registrationConfirmation;
                }).orElse(null);
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
