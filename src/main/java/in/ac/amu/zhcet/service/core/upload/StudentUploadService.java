package in.ac.amu.zhcet.service.core.upload;

import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.dto.StudentUpload;
import in.ac.amu.zhcet.data.repository.DepartmentRepository;
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
import java.util.Optional;

import static in.ac.amu.zhcet.utils.Utils.capitalizeAll;
import static in.ac.amu.zhcet.utils.Utils.capitalizeFirst;

@Slf4j
@Service
public class StudentUploadService {
    private boolean invalidDepartment;
    private boolean duplicateFacultyNo;
    private boolean duplicateEnrolmentNo;

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
        student.getUser().getDetails().setDepartment(new Department(studentUpload.getDepartment()));

        return student;
    }

    private String getMappedValue(Student student, List<Department> departments) {
        String departmentName = capitalizeFirst(student.getUser().getDetails().getDepartment().getName());

        Optional<Department> optional = departments.stream()
                .filter(department -> department.getName().equals(departmentName))
                .findFirst();

        if (!optional.isPresent()) {
            invalidDepartment = true;
            return  "No such department: " + departmentName;
        } else if (studentService.getByEnrolmentNumber(student.getEnrolmentNumber()) != null) {
            duplicateEnrolmentNo = true;
            return  "Duplicate enrolment number";
        } else if (studentService.getByFacultyNumber(student.getFacultyNumber()) != null) {
            duplicateFacultyNo = true;
            return "Duplicate faculty number";
        } else {
            student.getUser().getDetails().setDepartment(optional.get());
            return null;
        }
    }

    public Confirmation<Student, String> confirmUpload(UploadResult<StudentUpload> uploadResult) {
        invalidDepartment = false;
        duplicateFacultyNo = false;
        duplicateEnrolmentNo = false;

        List<Department> departments = departmentRepository.findAll();

        Confirmation<Student, String> studentConfirmation = uploadService.confirmUpload(
                uploadResult,
                StudentUploadService::fromStudentUpload,
                student -> getMappedValue(student, departments)
        );

        if (invalidDepartment)
            studentConfirmation.getErrors().add("Students with invalid department found");
        if (duplicateEnrolmentNo)
            studentConfirmation.getErrors().add("Students with duplicate enrolment found");
        if (duplicateFacultyNo)
            studentConfirmation.getErrors().add("Students with duplicate faculty number found");

        return studentConfirmation;
    }

    @Transactional
    public void registerStudents(Confirmation<Student, String> confirmation) {
        for (Student student : confirmation.getData().keySet()) {
            studentService.register(student);
        }
    }
}
