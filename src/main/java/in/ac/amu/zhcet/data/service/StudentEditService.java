package in.ac.amu.zhcet.data.service;

import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.base.user.UserAuth;
import in.ac.amu.zhcet.data.model.dto.StudentEditModel;
import in.ac.amu.zhcet.utils.DuplicateException;
import in.ac.amu.zhcet.utils.Utils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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

        return studentEditModel;
    }

    private void mergeModel(Student student, StudentEditModel studentEditModel, Department department) {
        student.setFacultyNumber(studentEditModel.getFacultyNumber());
        student.getUser().setName(studentEditModel.getName());
        student.getUser().setEmail(studentEditModel.getEmail());
        student.getUser().getDetails().setDepartment(department);
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
        UserAuth checkEmailDuplicate = userService.getUserByEmail(studentEditModel.getEmail());
        if (checkEmailDuplicate != null && !checkEmailDuplicate.getUserId().equals(student.getUser().getUserId()))
            throw new DuplicateException("User", "email", studentEditModel.getEmail(), studentEditModel);

        if (!Utils.isEmpty(studentEditModel.getEmail()) && !Utils.isValidEmail(studentEditModel.getEmail()))
            throw new RuntimeException("Invalid Email");

        mergeModel(student, studentEditModel, department);
        studentService.save(student);
    }

}
