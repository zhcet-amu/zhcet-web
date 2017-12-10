package in.ac.amu.zhcet.service;

import com.google.common.base.Strings;
import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.dto.datatables.StudentEditModel;
import in.ac.amu.zhcet.data.type.HallCode;
import in.ac.amu.zhcet.data.type.StudentStatus;
import in.ac.amu.zhcet.utils.exception.DuplicateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

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
        Optional<Student> studentOptional = studentService.getByEnrolmentNumber(id);

        studentOptional.orElseThrow(() -> {
            log.error("Tried saving non-existent student {}", id);
            return new UsernameNotFoundException("Invalid Request");
        });

        studentOptional.ifPresent(student -> {
            String departmentName = studentEditModel.getUserDepartmentName();
            Optional<Department> departmentOptional = departmentService.findByName(departmentName);
            departmentOptional.orElseThrow(() -> {
                log.error("Tried saving student with non-existent department {} {}", id, departmentName);
                return new RuntimeException("No such department : " + departmentName);
            });

            departmentOptional.ifPresent(department -> {
                Optional<Student> checkDuplicate = studentService.getByFacultyNumber(studentEditModel.getFacultyNumber());
                if (checkDuplicate.isPresent() && !checkDuplicate.get().getUser().getUserId().equals(student.getUser().getUserId())) {
                    log.error("Tried to save student with duplicate faculty number {} {}", id, studentEditModel.getFacultyNumber());
                    throw new DuplicateException("Student", "Faculty Number", studentEditModel.getFacultyNumber(), studentEditModel);
                }

                if (!studentEditModel.getUserEmail().equals(student.getUser().getEmail())) {
                    if (userService.throwDuplicateEmail(studentEditModel.getUserEmail(), student.getUser()))
                        studentEditModel.setUserEmail(null);
                    student.getUser().setEmailVerified(false);
                }

                if (!Strings.isNullOrEmpty(studentEditModel.getHallCode()) && !EnumUtils.isValidEnum(HallCode.class, studentEditModel.getHallCode())) {
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
            });
        });
    }

    private void studentConsumer(List<String> enrolments, Consumer<Student> consumer) {
        enrolments.stream()
                .map(studentService::getByEnrolmentNumber)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(student -> {
                    consumer.accept(student);
                    studentService.save(student);
                });
    }

    @Transactional
    public void changeSections(List<String> enrolments, String section) {
        if (section == null || section.length() < 3)
            throw new IllegalStateException("Section should be of at least 3 characters");
        if (enrolments.size() > 200)
            throw new IllegalStateException("Cannot update more than 200 students at a time");

        log.info("Changing sections of {} to {}", enrolments, section);
        studentConsumer(enrolments, student -> student.setSection(section));
    }

    @Transactional
    public void changeStatuses(List<String> enrolments, String status) {
        if (status == null || status.length() < 1)
            throw new IllegalStateException("Status should be of 1 character");
        if (enrolments.size() > 200)
            throw new IllegalStateException("Cannot update more than 200 students at a time");

        log.info("Changing statuses of {} to {}", enrolments, status);
        studentConsumer(enrolments, student -> student.setStatus(status.charAt(0)));
    }
}
