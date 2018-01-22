package amu.zhcet.core.admin.dean.registration.student;

import amu.zhcet.common.utils.StringUtils;
import amu.zhcet.data.department.Department;
import amu.zhcet.data.department.DepartmentRepository;
import amu.zhcet.data.user.UserRepository;
import amu.zhcet.data.user.UserService;
import amu.zhcet.data.user.student.Student;
import amu.zhcet.data.user.student.StudentRepository;
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
import java.util.stream.Collectors;

@Slf4j
@Service
class StudentRegistrationAdapter {

    private final UserService userService;
    private final StudentService studentService;
    private final DepartmentRepository departmentRepository;
    private final AbstractUploadService<StudentUpload, Student> uploadService;

    @Autowired
    public StudentRegistrationAdapter(UserService userService, DepartmentRepository departmentRepository, StudentService studentService, AbstractUploadService<StudentUpload, Student> uploadService) {
        this.userService = userService;
        this.departmentRepository = departmentRepository;
        this.studentService = studentService;
        this.uploadService = uploadService;
    }

    UploadResult<StudentUpload> fileToUpload(MultipartFile file) throws IOException {
        return uploadService.handleUpload(StudentUpload.class, file);
    }

    Confirmation<Student> confirmUpload(UploadResult<StudentUpload> uploadResult) {
        StudentIntegrityVerifier verifier = getVerifier(uploadResult);

        Confirmation<Student> studentConfirmation = uploadService.confirmUpload(uploadResult)
                        .convert(StudentRegistrationAdapter::fromStudentUpload)
                        .map(verifier::getError)
                        .get();

        StudentIntegrityVerifier.ErrorConditions conditions = verifier.getErrorConditions();
        if (conditions.isInvalidDepartment())
            studentConfirmation.getErrors().add("Students with invalid department found");
        if (conditions.isDuplicateEnrolmentNo())
            studentConfirmation.getErrors().add("Students with duplicate enrolment found");
        if (conditions.isDuplicateFacultyNo())
            studentConfirmation.getErrors().add("Students with duplicate faculty number found");

        if (!studentConfirmation.getErrors().isEmpty()) {
            log.warn(studentConfirmation.getErrors().toString());
        }

        return studentConfirmation;
    }

    private StudentIntegrityVerifier getVerifier(UploadResult<StudentUpload> uploadResult) {
        List<Department> departments = departmentRepository.findAll();
        List<UserRepository.Identifier> existingUserIds = userService.getUserIdentifiers(getEnrolments(uploadResult));
        List<StudentRepository.Identifier> existingFacultyNumbers = studentService.getIdentifiersByFacultyNumbers(getFacultyNumbers(uploadResult));

        return new StudentIntegrityVerifier(departments, existingUserIds, existingFacultyNumbers);
    }

    private static List<String> getFacultyNumbers(UploadResult<StudentUpload> uploadResult) {
        return uploadResult.getUploads()
                .stream()
                .map(StudentUpload::getFacultyNumber)
                .collect(Collectors.toList());
    }

    private static List<String> getEnrolments(UploadResult<StudentUpload> uploadResult) {
        return uploadResult.getUploads()
                .stream()
                .map(StudentUpload::getEnrolmentNumber)
                .collect(Collectors.toList());
    }

    private static Student fromStudentUpload(StudentUpload studentUpload) {
        Student student = new Student();
        student.setEnrolmentNumber(studentUpload.getEnrolmentNumber());
        student.setFacultyNumber(studentUpload.getFacultyNumber());
        student.getUser().setName(studentUpload.getName());
        Department department = Department.builder()
                .name(StringUtils.capitalizeFirst(studentUpload.getDepartment()))
                .build();
        student.getUser().setDepartment(department);
        student.setSection(studentUpload.getSection());
        student.setHallCode(studentUpload.getHall());
        student.setRegistrationYear(studentUpload.getRegistrationYear());
        student.setStatus(studentUpload.getStatus());
        student.getUser().getDetails().setGender(studentUpload.getGender());

        return student;
    }

}
