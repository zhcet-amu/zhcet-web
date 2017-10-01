package in.ac.amu.zhcet.service.core.upload;

import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.dto.RegistrationUpload;
import in.ac.amu.zhcet.service.core.FloatedCourseService;
import in.ac.amu.zhcet.service.core.StudentService;
import in.ac.amu.zhcet.service.core.upload.base.Confirmation;
import in.ac.amu.zhcet.service.core.upload.base.AbstractUploadService;
import in.ac.amu.zhcet.service.core.upload.base.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private final FloatedCourseService floatedCourseService;
    private final AbstractUploadService<RegistrationUpload, Student, String> uploadService;

    @Autowired
    public RegistrationUploadService(StudentService studentService, FloatedCourseService floatedCourseService, AbstractUploadService<RegistrationUpload, Student, String> uploadService) {
        this.studentService = studentService;
        this.floatedCourseService = floatedCourseService;
        this.uploadService = uploadService;
    }

    public UploadResult<RegistrationUpload> handleUpload(MultipartFile file) throws IOException {
        return uploadService.handleUpload(RegistrationUpload.class, file);
    }

    private Student fromRegistrationUpload(RegistrationUpload upload) {
        Student student = studentService.getByFacultyNumber(capitalizeAll(upload.getFacultyNo()));

        if (student == null)
            return Student.builder().facultyNumber(upload.getFacultyNo()).build();

        student.setMeta(String.valueOf(upload.getMode()));
        return student;
    }

    private String getMappedValue(Student student, String courseId, List<CourseRegistration> registrations) {
        if (student.getEnrolmentNumber() == null) {
            invalidEnrolment = true;
            return  "No such student found";
        } else if(registrations.stream()
                .map(CourseRegistration::getStudent)
                .anyMatch(oldStudent -> oldStudent.equals(student))) {
            alreadyEnrolled = true;
            return "Already enrolled in " + courseId;
        } else {
            return null;
        }
    }

    public Confirmation<Student, String> confirmUpload(String courseId, UploadResult<RegistrationUpload> uploadResult) {
        invalidEnrolment = false;
        alreadyEnrolled = false;

        List<CourseRegistration> registrations = floatedCourseService.getCourseById(courseId).getCourseRegistrations();

        Confirmation<Student, String> registrationConfirmation = uploadService.confirmUpload(
                uploadResult,
                this::fromRegistrationUpload,
                student -> getMappedValue(student, courseId, registrations)
        );

        if (invalidEnrolment)
            registrationConfirmation.getErrors().add("Invalid student faculty number found");
        if (alreadyEnrolled)
            registrationConfirmation.getErrors().add("Students already enrolled in course found");

        return registrationConfirmation;
    }

    @Transactional
    public void registerStudents(String courseId, List<String> studentIds, List<String> modes) {
        floatedCourseService.registerStudents(courseId, studentIds, modes);
    }

}
