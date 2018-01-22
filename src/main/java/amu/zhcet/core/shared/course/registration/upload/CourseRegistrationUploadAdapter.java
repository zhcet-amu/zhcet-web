package amu.zhcet.core.shared.course.registration.upload;

import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.course.floated.FloatedCourseService;
import amu.zhcet.data.course.registration.CourseRegistration;
import amu.zhcet.data.user.student.Student;
import amu.zhcet.data.user.student.StudentService;
import amu.zhcet.storage.csv.AbstractUploadService;
import amu.zhcet.storage.csv.Confirmation;
import amu.zhcet.storage.csv.UploadResult;
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
        List<CourseRegistration> registrations = floatedCourseService.getFloatedCourse(course)
                .map(FloatedCourse::getCourseRegistrations)
                .orElse(null);

        if (registrations == null)
            return null;

        CourseRegistrationIntegrityVerifier verifier = new CourseRegistrationIntegrityVerifier(course, registrations);

        Confirmation<CourseRegistration> confirmation = uploadService.confirmUpload(uploadResult)
                .convert(this::fromRegistrationUpload)
                .map(courseRegistration -> verifier.getError(courseRegistration.getStudent()))
                .get();

        CourseRegistrationIntegrityVerifier.ErrorConditions conditions = verifier.getErrorConditions();

        if (conditions.isDuplicateFacultyNo())
            confirmation.getErrors().add("Duplicate Faculty Number found");
        if (conditions.isInvalidEnrolment())
            confirmation.getErrors().add("Invalid student faculty number found");
        if (conditions.isAlreadyEnrolled())
            confirmation.getErrors().add("Students already enrolled in course found");

        if (!confirmation.getErrors().isEmpty()) {
            log.warn(confirmation.getErrors().toString());
        }

        return confirmation;
    }

    private CourseRegistration fromRegistrationUpload(RegistrationUpload upload) {
        Student student = studentService.getByFacultyNumber(upload.getFacultyNo())
                .orElseGet(() -> Student.builder()
                        .facultyNumber(upload.getFacultyNo())
                        .build());

        CourseRegistration courseRegistration = new CourseRegistration();
        courseRegistration.setMode(upload.getMode());
        courseRegistration.setStudent(student);

        return courseRegistration;
    }

}
