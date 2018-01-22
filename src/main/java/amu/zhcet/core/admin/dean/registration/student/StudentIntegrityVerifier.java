package amu.zhcet.core.admin.dean.registration.student;

import amu.zhcet.core.admin.dean.registration.UserRegistrationUtils;
import amu.zhcet.data.department.Department;
import amu.zhcet.data.user.UserRepository;
import amu.zhcet.data.user.student.Student;
import amu.zhcet.data.user.student.StudentRepository;
import lombok.Data;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A scoped class which checks a bunch of validity constraints for a stream of students.
 * To be used before saving new Students in the database in bulk, i.e, after CSV processing
 * Checks if there is any local (CSV) or DB duplicate data to prevent constraint violations on persistence
 * Checks if Department name provided does not exist
 *
 * All the data required for checking is to be provided externally. The class does not load anything from database
 */
class StudentIntegrityVerifier {

    @Data
    static class ErrorConditions {
        private boolean invalidDepartment;
        private boolean duplicateEnrolmentNo;
        private boolean duplicateFacultyNo;
    }

    private final ErrorConditions conditions = new ErrorConditions();
    private final List<Department> departments;
    private final List<UserRepository.Identifier> userIds;
    private final List<StudentRepository.Identifier> facultyNumbers;

    private final Map<String, Boolean> usedFacultyNumbers = new HashMap<>();
    private final Map<String, Boolean> usedEnrolmentNumbers = new HashMap<>();

    StudentIntegrityVerifier(List<Department> departments, List<UserRepository.Identifier> userIds, List<StudentRepository.Identifier> facultyNumbers) {
        Assert.notNull(departments, "Departments should not be null");
        Assert.notNull(userIds, "User IDs should not be null");
        Assert.notNull(facultyNumbers, "Faculty Numbers should not be null");

        this.departments = departments;
        this.userIds = userIds;
        this.facultyNumbers = facultyNumbers;
    }

    /**
     * Checks a student across various constraints and sets error values accordingly
     * @param student Student to be checked
     * @return String null if no violation was found, or denoting the kind of violation
     */
    public String getError(Student student) {
        Assert.notNull(student, "Student should not be null");

        if (isDuplicateFacultyNumber(student)) {
            return "Duplicate Faculty Number";
        } else if (isDuplicateEnrolmentNumber(student)) {
            return "Duplicate Enrolment Number";
        } else if (!doesDepartmentExist(student)) {
            return "No such department: " + student.getUser().getDepartment().getName();
        } else if (doesEnrolmentAlreadyExist(student)) {
            return "Student with enrolment number already exists";
        } else if (doesFacultyNumberAlreadyExist(student)) {
            return "Student with faculty number already exists";
        } else {
            return null;
        }
    }

    /**
     * Error conditions that occured while parsing
     * @return ErrorConditions
     */
    public ErrorConditions getErrorConditions() {
        return conditions;
    }

    /**
     * Checks if the student's faculty number was already seen while parsing to detect duplicates in current scope
     * @param student Student to be checked
     * @return boolean true if the student faculty number was seen while parsing, else false
     */
    private boolean isDuplicateFacultyNumber(Student student) {
        String key = student.getFacultyNumber();
        if (usedFacultyNumbers.containsKey(key)) {
            conditions.setDuplicateFacultyNo(true);
            return true;
        }

        usedFacultyNumbers.put(key, true);
        return false;
    }

    /**
     * Checks if the student's enrolment number was already seen while parsing to detect duplicates in current scope
     * @param student Student to be checked
     * @return boolean true if the student enrolment number was seen while parsing, else false
     */
    private boolean isDuplicateEnrolmentNumber(Student student) {
        String key = student.getEnrolmentNumber();
        if (usedEnrolmentNumbers.containsKey(key)) {
            conditions.setDuplicateEnrolmentNo(true);
            return true;
        }

        usedEnrolmentNumbers.put(key, true);
        return false;
    }

    /**
     * Checks if the department provided in Student exists in the available departments
     * @param student Student containing the department name
     * @return boolean true if department exists, else false
     */
    private boolean doesDepartmentExist(Student student) {
        boolean exists = UserRegistrationUtils.departmentExists(student.getUser(), departments);
        conditions.setInvalidDepartment(!exists);
        return exists;
    }

    /**
     * Checks if the enrolment number already exists among the saved students
     * @param student Student whose enrolment number is to be checked
     * @return boolean true if enrolment number already exists among saved, else false
     */
    private boolean doesEnrolmentAlreadyExist(Student student) {
        boolean exists = UserRegistrationUtils.userIdExists(student.getEnrolmentNumber(), userIds);
        if (exists) conditions.setDuplicateEnrolmentNo(true);
        return exists;
    }

    /**
     * Checks if the faculty number already exists among the saved students
     * @param student Student whose faculty number is to be checked
     * @return boolean true if faculty number already exists among saved, else false
     */
    private boolean doesFacultyNumberAlreadyExist(Student student) {
        boolean exists = facultyNumbers.stream()
                .anyMatch(identifier -> identifier.getFacultyNumber()
                        .equals(student.getFacultyNumber()));

        if (exists) conditions.setDuplicateFacultyNo(true);
        return exists;
    }
}
