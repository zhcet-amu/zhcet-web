package in.ac.amu.zhcet.service.upload.csv;

import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.dto.upload.StudentUpload;
import in.ac.amu.zhcet.data.repository.DepartmentRepository;
import in.ac.amu.zhcet.data.repository.StudentRepository;
import in.ac.amu.zhcet.service.StudentService;
import in.ac.amu.zhcet.service.realtime.RealTimeStatus;
import in.ac.amu.zhcet.service.realtime.RealTimeStatusService;
import in.ac.amu.zhcet.service.upload.csv.base.AbstractUploadService;
import in.ac.amu.zhcet.service.upload.csv.base.Confirmation;
import in.ac.amu.zhcet.service.upload.csv.base.UploadResult;
import in.ac.amu.zhcet.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class StudentUploadService {
    private boolean invalidDepartment;
    private boolean duplicateFacultyNo;
    private boolean duplicateEnrolmentNo;
    private boolean invalidHallCode;

    private final DepartmentRepository departmentRepository;
    private final StudentService studentService;
    private final RealTimeStatusService realTimeStatusService;
    private final AbstractUploadService<StudentUpload, Student> uploadService;

    @Autowired
    public StudentUploadService(DepartmentRepository departmentRepository, StudentService studentService, RealTimeStatusService realTimeStatusService, AbstractUploadService<StudentUpload, Student> uploadService) {
        this.departmentRepository = departmentRepository;
        this.studentService = studentService;
        this.realTimeStatusService = realTimeStatusService;
        this.uploadService = uploadService;
    }

    public UploadResult<StudentUpload> handleUpload(MultipartFile file) throws IOException {
        return uploadService.handleUpload(StudentUpload.class, file);
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

    private String getMappedValue(Student student, List<Department> departments, List<StudentRepository.Identifier> identifiers) {
        String departmentName = student.getUser().getDepartment().getName();

        Optional<Department> optional = departments.stream()
                .filter(department -> department.getName().equals(departmentName))
                .findFirst();

        if (!optional.isPresent()) {
            invalidDepartment = true;
            return  "No such department: " + departmentName;
        } else if (identifiers.parallelStream().anyMatch(identifier -> identifier.getEnrolmentNumber().equals(student.getEnrolmentNumber()))) {
            duplicateEnrolmentNo = true;
            return  "Duplicate enrolment number";
        } else if (identifiers.parallelStream().anyMatch(identifier -> identifier.getFacultyNumber().equals(student.getFacultyNumber()))) {
            duplicateFacultyNo = true;
            return "Duplicate faculty number";
        } else if (student.getHallCode().length() > 2) {
            invalidHallCode = true;
            return "Invalid Hall Code : " + student.getHallCode();
        } else {
            student.getUser().setDepartment(optional.get());
            return null;
        }
    }

    public Confirmation<Student> confirmUpload(UploadResult<StudentUpload> uploadResult) {
        invalidDepartment = false;
        duplicateFacultyNo = false;
        duplicateEnrolmentNo = false;
        invalidHallCode = false;

        List<Department> departments = departmentRepository.findAll();
        List<StudentRepository.Identifier> identifiers = studentService.getAllIdentifiers();

        Confirmation<Student> studentConfirmation = uploadService.confirmUpload(
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
        }

        return studentConfirmation;
    }

    public RealTimeStatus registerStudents(Confirmation<Student> confirmation) {
        RealTimeStatus status = realTimeStatusService.install();
        studentService.register(confirmation.getData(), status);

        return status;
    }
}
