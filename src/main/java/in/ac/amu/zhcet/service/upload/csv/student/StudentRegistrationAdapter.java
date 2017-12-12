package in.ac.amu.zhcet.service.upload.csv.student;

import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.dto.upload.StudentUpload;
import in.ac.amu.zhcet.data.repository.DepartmentRepository;
import in.ac.amu.zhcet.data.repository.StudentRepository;
import in.ac.amu.zhcet.data.repository.UserRepository;
import in.ac.amu.zhcet.service.StudentService;
import in.ac.amu.zhcet.service.UserService;
import in.ac.amu.zhcet.service.upload.csv.AbstractUploadService;
import in.ac.amu.zhcet.service.upload.csv.Confirmation;
import in.ac.amu.zhcet.service.upload.csv.UploadResult;
import in.ac.amu.zhcet.utils.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StudentRegistrationAdapter {

    private final UserService userService;
    private final StudentService studentService;
    private final DepartmentRepository departmentRepository;
    private final AbstractUploadService<StudentUpload, Student> uploadService;

    @Data
    private static class ErrorConditions {
        private boolean invalidDepartment;
        private boolean invalidHallCode;
        private boolean duplicateEnrolmentNo;
        private boolean duplicateFacultyNo;
    }

    @Autowired
    public StudentRegistrationAdapter(UserService userService, DepartmentRepository departmentRepository, StudentService studentService, AbstractUploadService<StudentUpload, Student> uploadService) {
        this.userService = userService;
        this.departmentRepository = departmentRepository;
        this.studentService = studentService;
        this.uploadService = uploadService;
    }

    UploadResult<StudentUpload> fileToUppload(MultipartFile file) throws IOException {
        return uploadService.handleUpload(StudentUpload.class, file);
    }

    Confirmation<Student> confirmUpload(UploadResult<StudentUpload> uploadResult) {
        ErrorConditions conditions = new ErrorConditions();

        List<Department> departments = departmentRepository.findAll();

        List<String> enrolments = uploadResult.getUploads()
                .stream()
                .map(StudentUpload::getEnrolmentNo)
                .collect(Collectors.toList());

        List<String> facultyNumbers = uploadResult.getUploads()
                .stream()
                .map(StudentUpload::getFacultyNo)
                .collect(Collectors.toList());

        List<UserRepository.Identifier> existingUserIds = userService.getUserIdentifiers(enrolments);
        List<StudentRepository.Identifier> existingFacultyNumbers = studentService.getIdentifiersByFacultyNummbers(facultyNumbers);

        log.warn("Duplicate enrolments : {}", existingUserIds.toString());
        log.warn("Duplicate facultyNumbers : {}", existingFacultyNumbers.toString());

        Confirmation<Student> studentConfirmation =
                uploadService.confirmUpload(uploadResult)
                        .convert(StudentRegistrationAdapter::fromStudentUpload)
                        .map(student -> getMappedValue(student, departments, existingUserIds, existingFacultyNumbers, conditions))
                        .get();

        if (conditions.isInvalidDepartment())
            studentConfirmation.getErrors().add("Students with invalid department found");
        if (conditions.isDuplicateEnrolmentNo())
            studentConfirmation.getErrors().add("Students with duplicate enrolment found");
        if (conditions.isDuplicateFacultyNo())
            studentConfirmation.getErrors().add("Students with duplicate faculty number found");
        if (conditions.isInvalidHallCode())
            studentConfirmation.getErrors().add("Students with invalid hall code found. Hall Code should be of 2 characters");

        if (!studentConfirmation.getErrors().isEmpty()) {
            log.warn(studentConfirmation.getErrors().toString());
        }

        return studentConfirmation;
    }

    private String getMappedValue(
            Student student,
            List<Department> departments,
            List<UserRepository.Identifier> userIds,
            List<StudentRepository.Identifier> facultyNumbers,
            ErrorConditions conditions
    ) {
        String departmentName = student.getUser().getDepartment().getName();

        Optional<Department> optional = departments.stream()
                .filter(department -> department.getName().equals(departmentName))
                .findFirst();

        if (!optional.isPresent()) {
            conditions.setInvalidDepartment(true);
            return  "No such department: " + departmentName;
        } else if (userIds.parallelStream().anyMatch(identifier -> identifier.getUserId().equals(student.getEnrolmentNumber()))) {
            conditions.setDuplicateEnrolmentNo(true);
            return  "Duplicate enrolment number";
        } else if (facultyNumbers.parallelStream().anyMatch(identifier -> identifier.getFacultyNumber().equals(student.getFacultyNumber()))) {
            conditions.setDuplicateFacultyNo(true);
            return "Duplicate faculty number";
        } else if (student.getHallCode().length() > 2) {
            conditions.setInvalidHallCode(true);
            return "Invalid Hall Code : " + student.getHallCode();
        } else {
            student.getUser().setDepartment(optional.get());
            return null;
        }
    }

    private static Student fromStudentUpload(StudentUpload studentUpload) {
        Student student = new Student();
        student.setEnrolmentNumber(StringUtils.capitalizeAll(studentUpload.getEnrolmentNo()));
        student.setFacultyNumber(StringUtils.capitalizeAll(studentUpload.getFacultyNo()));
        student.getUser().setName(StringUtils.capitalizeFirst(studentUpload.getName()));
        student.getUser().setDepartment(Department.builder().name(StringUtils.capitalizeFirst(studentUpload.getDepartment())).build());
        student.setSection(StringUtils.capitalizeAll(studentUpload.getSection()));
        student.setHallCode(StringUtils.capitalizeAll(studentUpload.getHall()));
        student.setRegistrationYear(studentUpload.getRegistrationYear());
        student.setStatus(studentUpload.getStatus());
        student.getUser().getDetails().setGender(studentUpload.getGender());

        return student;
    }

}
