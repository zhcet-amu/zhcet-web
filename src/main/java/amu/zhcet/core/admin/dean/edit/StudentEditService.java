package amu.zhcet.core.admin.dean.edit;

import amu.zhcet.common.error.DuplicateException;
import amu.zhcet.data.department.Department;
import amu.zhcet.data.department.DepartmentService;
import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserService;
import amu.zhcet.data.user.student.HallCode;
import amu.zhcet.data.user.student.Student;
import amu.zhcet.data.user.student.StudentService;
import amu.zhcet.data.user.student.StudentStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@Service
class StudentEditService {

    private static final int MAXIMUM_STUDENT_UPDATE_SIZE = 200;

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
    public void saveStudent(Student student, StudentEditModel studentEditModel) {
        Department department = ModelEditUtils.verifyDepartment(studentEditModel.getUserDepartmentName(), departmentService::findByName);

        checkFacultyNumber(student, studentEditModel);
        studentEditModel.setUserEmail(ModelEditUtils.verifyNewEmail(
                student::getUser,
                studentEditModel::getUserEmail,
                userService::checkDuplicateEmail
        ));

        checkHallCode(studentEditModel);
        checkStatus(studentEditModel);

        student.getUser().setDepartment(department);
        modelMapper.map(studentEditModel, student);
        studentService.save(student);
    }

    private void checkFacultyNumber(Student student, StudentEditModel studentEditModel) {
        String facultyNumber = studentEditModel.getFacultyNumber();
        studentService.getByFacultyNumber(facultyNumber)
                .map(Student::getUser)
                .map(User::getUserId)
                .filter(id -> !id.equals(student.getUser().getUserId()))
                .ifPresent(duplicate -> {
                    log.warn("Tried to save student with duplicate faculty number {} -> {} (existing)", facultyNumber, duplicate);
                    throw new DuplicateException("Student", "Faculty Number", facultyNumber, studentEditModel);
                });
    }

    private static void checkHallCode(StudentEditModel studentEditModel) {
        checkMemberShip(HallCode.class, studentEditModel.getHallCode(),
                "Hall Code", studentEditModel.getFacultyNumber());
    }

    private static void checkStatus(StudentEditModel studentEditModel) {
        checkMemberShip(StudentStatus.class, String.valueOf(studentEditModel.getStatus()),
                "Status", studentEditModel.getFacultyNumber());
    }

    private static <E extends Enum<E>> void checkMemberShip(Class<E> enumClass, String string, String label, String identifier) {
        if (!EnumUtils.isValidEnum(enumClass, string)) {
            log.warn("Tried to save student with invalid status {} {}", identifier, string);
            throw new IllegalArgumentException("Invalid " + label + " : " + string + ". Must be within " + EnumUtils.getEnumMap(enumClass).keySet());
        }
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

    private static void checkStudentsSize(List<String> items) {
        if (items.size() > MAXIMUM_STUDENT_UPDATE_SIZE)
            throw new IllegalArgumentException("Cannot update more than " + MAXIMUM_STUDENT_UPDATE_SIZE + " students at a time");
    }

    @Transactional
    public void changeSections(List<String> enrolments, String section) {
        checkStudentsSize(enrolments);
        if (section == null || section.length() < 3)
            throw new IllegalArgumentException("Section should be of at least 3 characters");

        log.info("Changing sections of {} to {}", enrolments, section);
        studentConsumer(enrolments, student -> student.setSection(section));
    }

    @Transactional
    public void changeStatuses(List<String> enrolments, String status) {
        checkStudentsSize(enrolments);
        if (status == null || status.length() != 1)
            throw new IllegalArgumentException("Status should be of 1 character");

        log.info("Changing statuses of {} to {}", enrolments, status);
        studentConsumer(enrolments, student -> student.setStatus(status.charAt(0)));
    }
}
