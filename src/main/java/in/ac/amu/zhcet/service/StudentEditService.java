package in.ac.amu.zhcet.service;

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
import java.util.List;

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

        if (student == null) {
            log.error("Tried saving non-existent student {}", id);
            throw new UsernameNotFoundException("Invalid Request");
        }

        String departmentName = studentEditModel.getUserDepartmentName();
        Department department = departmentService.findByName(departmentName);
        if (department == null) {
            log.error("Tried saving student with non-existent department {} {}", id, departmentName);
            throw new RuntimeException("No such department : " + departmentName);
        }

        Student checkDuplicate = studentService.getByFacultyNumber(studentEditModel.getFacultyNumber());
        if (checkDuplicate != null && !checkDuplicate.getUser().getUserId().equals(student.getUser().getUserId())) {
            log.error("Tried to save student with duplicate faculty number {} {}", id, studentEditModel.getFacultyNumber());
            throw new DuplicateException("Student", "Faculty Number", studentEditModel.getFacultyNumber(), studentEditModel);
        }

        if (!studentEditModel.getUserEmail().equals(student.getUser().getEmail())) {
            if (userService.throwDuplicateEmail(studentEditModel.getUserEmail(), student.getUser()))
                studentEditModel.setUserEmail(null);
            else {
                student.getUser().setActive(false);
            }
        }

        if (!Utils.isEmpty(studentEditModel.getHallCode()) && !EnumUtils.isValidEnum(HallCode.class, studentEditModel.getHallCode())) {
            log.error("Tried to save student with invalid hall code {} {}", id, studentEditModel.getHallCode());
            throw new RuntimeException("Invalid Hall : " + studentEditModel.getHallCode() + ". Must be within " + EnumUtils.getEnumMap(HallCode.class).keySet());
        }

        if (studentEditModel.getStatus() != null && !EnumUtils.isValidEnum(StudentStatus.class, studentEditModel.getStatus()+"")) {
            log.error("Tried to save student with invalid status {} {}", id, studentEditModel.getStatus());
            throw new RuntimeException("Invalid Status : " + studentEditModel.getStatus() + ". Must be within " + EnumUtils.getEnumMap(StudentStatus.class).keySet());
        }

        student.getUser().setDepartment(department);
        modelMapper.map(studentEditModel, student);
        studentService.save(student);
    }

    @Transactional
    public void changeSections(List<String> enrolments, String section) {
        if (enrolments.size() > 200)
            throw new IllegalStateException("Cannot update more than 200 students at a time");

        for (String enrolment : enrolments) {
            Student student = studentService.getByEnrolmentNumber(enrolment);
            student.setSection(section);
            studentService.save(student);
        }
    }

    @Transactional
    public void changeStatuses(List<String> enrolments, String status) {
        if (enrolments.size() > 200)
            throw new IllegalStateException("Cannot update more than 200 students at a time");

        for (String enrolment : enrolments) {
            Student student = studentService.getByEnrolmentNumber(enrolment);
            student.setStatus(status.charAt(0));
            studentService.save(student);
        }
    }
}
