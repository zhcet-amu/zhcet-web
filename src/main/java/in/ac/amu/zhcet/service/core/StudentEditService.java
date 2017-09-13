package in.ac.amu.zhcet.service.core;

import in.ac.amu.zhcet.data.HallCode;
import in.ac.amu.zhcet.data.StudentStatus;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.data.model.dto.StudentEditModel;
import in.ac.amu.zhcet.utils.DuplicateException;
import in.ac.amu.zhcet.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
public class StudentEditService {

    private final UserService userService;
    private final StudentService studentService;
    private final DepartmentService departmentService;

    public StudentEditService(UserService userService, StudentService studentService, DepartmentService departmentService) {
        this.userService = userService;
        this.studentService = studentService;
        this.departmentService = departmentService;
    }

    public StudentEditModel fromStudent(Student student) {
        StudentEditModel studentEditModel = new StudentEditModel();

        studentEditModel.setFacultyNumber(student.getFacultyNumber());
        studentEditModel.setName(student.getUser().getName());
        studentEditModel.setEmail(student.getUser().getEmail());
        studentEditModel.setDepartment(student.getUser().getDetails().getDepartment().getName());
        studentEditModel.setHallCode(student.getHallCode());
        studentEditModel.setSection(student.getSection());
        studentEditModel.setStatus(student.getStatus());

        return studentEditModel;
    }

    private void mergeModel(Student student, StudentEditModel studentEditModel, Department department) {
        student.setFacultyNumber(studentEditModel.getFacultyNumber());
        student.getUser().setName(studentEditModel.getName());
        student.getUser().setEmail(studentEditModel.getEmail());
        student.getUser().getDetails().setDepartment(department);
        student.setHallCode(studentEditModel.getHallCode());
        student.setSection(studentEditModel.getSection());
        student.setStatus(studentEditModel.getStatus());

        log.info(student.toString());
    }

    @Transactional
    public void saveStudent(String id, StudentEditModel studentEditModel) {
        Student student = studentService.getByEnrolmentNumber(id);

        if (student == null)
            throw new UsernameNotFoundException("Invalid Request");

        Department department = departmentService.findByName(studentEditModel.getDepartment());
        if (department == null)
            throw new RuntimeException("No such department :" + studentEditModel.getDepartment());

        Student checkDuplicate = studentService.getByFacultyNumber(studentEditModel.getFacultyNumber());
        if (checkDuplicate != null && !checkDuplicate.getUser().getUserId().equals(student.getUser().getUserId()))
            throw new DuplicateException("Student", "Faculty Number", studentEditModel.getFacultyNumber(), studentEditModel);

        if (!Utils.isEmpty(studentEditModel.getEmail())) {
            UserAuth checkEmailDuplicate = userService.getUserByEmail(studentEditModel.getEmail());
            if (checkEmailDuplicate != null && !checkEmailDuplicate.getUserId().equals(student.getUser().getUserId()))
                throw new DuplicateException("User", "email", studentEditModel.getEmail(), studentEditModel);
            if (!Utils.isValidEmail(studentEditModel.getEmail()))
                throw new RuntimeException("Invalid Email");
        } else {
            studentEditModel.setEmail(null);
        }

        if (!Utils.isEmpty(studentEditModel.getHallCode()) && !EnumUtils.isValidEnum(HallCode.class, studentEditModel.getHallCode()))
            throw new RuntimeException("Invalid Hall : " + studentEditModel.getHallCode() + ". Must be within " + EnumUtils.getEnumMap(HallCode.class).keySet());

        if (studentEditModel.getStatus() != null && !EnumUtils.isValidEnum(StudentStatus.class, studentEditModel.getStatus()+""))
            throw new RuntimeException("Invalid Hall : " + studentEditModel.getStatus() + ". Must be within " + EnumUtils.getEnumMap(StudentStatus.class).keySet());


        mergeModel(student, studentEditModel, department);
        studentService.save(student);
    }

}
