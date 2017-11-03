package in.ac.amu.zhcet.service.csv;

import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.dto.upload.StudentUpload;
import in.ac.amu.zhcet.data.repository.DepartmentRepository;
import in.ac.amu.zhcet.data.repository.StudentRepository;
import in.ac.amu.zhcet.service.StudentService;
import in.ac.amu.zhcet.service.csv.base.AbstractUploadService;
import in.ac.amu.zhcet.service.csv.base.Confirmation;
import in.ac.amu.zhcet.service.csv.base.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static in.ac.amu.zhcet.utils.Utils.capitalizeAll;
import static in.ac.amu.zhcet.utils.Utils.capitalizeFirst;

@Slf4j
@Service
public class StudentUploadService {
    private boolean invalidDepartment;
    private boolean duplicateFacultyNo;
    private boolean duplicateEnrolmentNo;
    private boolean invalidHallCode;

    private final DepartmentRepository departmentRepository;
    private final StudentService studentService;
    private final AbstractUploadService<StudentUpload, Student, String> uploadService;

    @Autowired
    public StudentUploadService(DepartmentRepository departmentRepository, StudentService studentService, AbstractUploadService<StudentUpload, Student, String> uploadService) {
        this.departmentRepository = departmentRepository;
        this.studentService = studentService;
        this.uploadService = uploadService;
    }

    public UploadResult<StudentUpload> handleUpload(MultipartFile file) throws IOException {
        return uploadService.handleUpload(StudentUpload.class, file);
    }

    private static Student fromStudentUpload(StudentUpload studentUpload) {
        Student student = new Student();
        student.setEnrolmentNumber(capitalizeAll(studentUpload.getEnrolmentNo()));
        student.setFacultyNumber(capitalizeAll(studentUpload.getFacultyNo()));
        student.getUser().setName(capitalizeFirst(studentUpload.getName()));
        student.getUser().setDepartment(Department.builder().name(capitalizeFirst(studentUpload.getDepartment())).build());
        student.setSection(capitalizeAll(studentUpload.getSection()));
        student.setHallCode(capitalizeAll(studentUpload.getHall()));
        student.setRegistrationYear(studentUpload.getRegistrationYear());
        student.setStatus(studentUpload.getStatus());
        student.getUser().getDetails().setGender(studentUpload.getGender());

        return student;
    }

    private String getMappedValue(Student student, List<Department> departments, List<StudentRepository.Identifier> identifiers) {
        String departmentName = student.getUser().getDepartment().getName();

        Optional<Department> optional = departments.stream()
                .filter(department -> department.getName().equals(departmentName))
                .findFirst();

        if (!optional.isPresent()) {
            invalidDepartment = true;
            log.info("Student Registration : Invalid Department {}", departmentName);
            return  "No such department: " + departmentName;
        } else if (identifiers.parallelStream().anyMatch(identifier -> identifier.getEnrolmentNumber().equals(student.getEnrolmentNumber()))) {
            duplicateEnrolmentNo = true;
            log.info("Duplicate Enrolment {}", student.getEnrolmentNumber());
            return  "Duplicate enrolment number";
        } else if (identifiers.parallelStream().anyMatch(identifier -> identifier.getFacultyNumber().equals(student.getFacultyNumber()))) {
            duplicateFacultyNo = true;
            log.info("Duplicate Faculty Number {}", student.getFacultyNumber());
            return "Duplicate faculty number";
        } else if (student.getHallCode().length() > 2) {
            invalidHallCode = true;
            log.info("Invalid Hall Code {}", student.getHallCode());
            return "Invalid Hall Code : " + student.getHallCode();
        } else {
            student.getUser().setDepartment(optional.get());
            return null;
        }
    }

    public Confirmation<Student, String> confirmUpload(UploadResult<StudentUpload> uploadResult) {
        invalidDepartment = false;
        duplicateFacultyNo = false;
        duplicateEnrolmentNo = false;
        invalidHallCode = false;

        List<Department> departments = departmentRepository.findAll();
        List<StudentRepository.Identifier> identifiers = studentService.getAllIdentifiers();

        Confirmation<Student, String> studentConfirmation = uploadService.confirmUpload(
                uploadResult,
                StudentUploadService::fromStudentUpload,
                student -> getMappedValue(student, departments, identifiers)
        );

        if (invalidDepartment)
            studentConfirmation.getErrors().add("Students with invalid department found");
        if (duplicateEnrolmentNo)
            studentConfirmation.getErrors().add("Students with duplicate enrolment found");
        if (duplicateFacultyNo)
            studentConfirmation.getErrors().add("Students with duplicate faculty number found");
        if (invalidHallCode)
            studentConfirmation.getErrors().add("Students with invalid hall code found. Hall Code should be of 2 characters");

        if (!studentConfirmation.getErrors().isEmpty()) {
            log.warn(studentConfirmation.getErrors().toString());
            log.warn(studentConfirmation.getData().toString());
        }

        return studentConfirmation;
    }

    public void registerStudents(Confirmation<Student, String> confirmation) {
        long start = System.currentTimeMillis();
        studentService.register(confirmation.getData().keySet());
        log.warn("Saved {} students in {} ms", confirmation.getData().size(), System.currentTimeMillis() - start);
    }
}
