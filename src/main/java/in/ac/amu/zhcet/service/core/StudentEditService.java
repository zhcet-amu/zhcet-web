package in.ac.amu.zhcet.service.core;

import in.ac.amu.zhcet.data.HallCode;
import in.ac.amu.zhcet.data.StudentStatus;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.dto.datatables.StudentEditModel;
import in.ac.amu.zhcet.utils.DuplicateException;
import in.ac.amu.zhcet.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
public class StudentEditService {

    private final ModelMapper modelMapper;
    private final UserService userService;
    private final StudentService studentService;
    private final DepartmentService departmentService;

    public StudentEditService(ModelMapper modelMapper, UserService userService, StudentService studentService, DepartmentService departmentService) {
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.studentService = studentService;
        this.departmentService = departmentService;
    }

    public StudentEditModel fromStudent(Student student) {
        return modelMapper.map(student, StudentEditModel.class);
    }

    @Transactional
    public void saveStudent(String id, StudentEditModel studentEditModel) {
        Student student = studentService.getByEnrolmentNumber(id);

        if (student == null)
            throw new UsernameNotFoundException("Invalid Request");

        Department department = departmentService.findByName(studentEditModel.getUserDepartmentName());
        if (department == null)
            throw new RuntimeException("No such department :" + studentEditModel.getUserDepartmentName());

        Student checkDuplicate = studentService.getByFacultyNumber(studentEditModel.getFacultyNumber());
        if (checkDuplicate != null && !checkDuplicate.getUser().getUserId().equals(student.getUser().getUserId()))
            throw new DuplicateException("Student", "Faculty Number", studentEditModel.getFacultyNumber(), studentEditModel);

        if (userService.throwDuplicateEmail(studentEditModel.getUserEmail(), student.getUser()))
            studentEditModel.setUserEmail(null);

        if (!Utils.isEmpty(studentEditModel.getHallCode()) && !EnumUtils.isValidEnum(HallCode.class, studentEditModel.getHallCode()))
            throw new RuntimeException("Invalid Hall : " + studentEditModel.getHallCode() + ". Must be within " + EnumUtils.getEnumMap(HallCode.class).keySet());

        if (studentEditModel.getStatus() != null && !EnumUtils.isValidEnum(StudentStatus.class, studentEditModel.getStatus()+""))
            throw new RuntimeException("Invalid Hall : " + studentEditModel.getStatus() + ". Must be within " + EnumUtils.getEnumMap(StudentStatus.class).keySet());

        student.getUser().setDepartment(department);
        modelMapper.map(studentEditModel, student);
        studentService.save(student);
    }

}
