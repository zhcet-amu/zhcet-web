package amu.zhcet.core.admin.dean.registration.faculty;

import amu.zhcet.core.admin.dean.registration.UserRegistrationUtils;
import amu.zhcet.data.department.Department;
import amu.zhcet.data.user.UserRepository;
import amu.zhcet.data.user.faculty.FacultyMember;
import lombok.Data;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A scoped class which checks a bunch of validity constraints for a stream of faculty members.
 * To be used before saving new Faculty Members in the database in bulk, i.e, after CSV processing
 * Checks if there is any local (CSV) or DB duplicate data to prevent constraint violations on persistence
 * Checks if Department name provided does not exist
 *
 * All the data required for checking is to be provided externally. The class does not load anything from database
 */
class FacultyIntegrityVerifier {

    @Data
    static class ErrorConditions {
        private boolean invalidDepartment;
        private boolean duplicateFacultyId;
    }

    private final ErrorConditions conditions = new ErrorConditions();
    private final List<Department> departments;
    private final List<UserRepository.Identifier> userIds;

    private final Map<String, Boolean> usedFacultyIds = new HashMap<>();

    FacultyIntegrityVerifier(List<Department> departments, List<UserRepository.Identifier> userIds) {
        Assert.notNull(departments, "Departments should not be null");
        Assert.notNull(userIds, "User IDs should not be null");

        this.departments = departments;
        this.userIds = userIds;
    }

    public String getError(FacultyMember facultyMember) {
        Assert.notNull(facultyMember, "Faculty Member should not be null");

        if (isDuplicateId(facultyMember)) {
            return "Duplicate Faculty ID";
        } else if (!doesDepartmentExist(facultyMember)) {
            return "No such department: " + facultyMember.getUser().getDepartment().getName();
        } else if (doesFacultyIdAlreadyExists(facultyMember)) {
            return "Faculty Member with ID already exists";
        } else {
            return null;
        }
    }

    public ErrorConditions getErrorConditions() {
        return conditions;
    }

    /**
     * Checks if the faculty member's ID was already seen while parsing to detect duplicates in current scope
     * @param facultyMember Faculty Member to be checked
     * @return boolean true if the faculty member ID was seen while parsing, else false
     */
    private boolean isDuplicateId(FacultyMember facultyMember) {
        String key = facultyMember.getFacultyId();
        if (usedFacultyIds.containsKey(key)) {
            conditions.setDuplicateFacultyId(true);
            return true;
        }

        usedFacultyIds.put(key, true);
        return false;
    }

    /**
     * Checks if the department provided in Faculty Member exists in the available departments
     * @param facultyMember Faculty Member containing the department name
     * @return boolean true if department exists, else false
     */
    private boolean doesDepartmentExist(FacultyMember facultyMember) {
        boolean exists = UserRegistrationUtils.departmentExists(facultyMember.getUser(), departments);
        if (!exists) conditions.setInvalidDepartment(true);
        return exists;
    }

    /**
     * Checks if the faculty IS already exists among the saved faculty
     * @param facultyMember Faculty Member whose ID is to be checked
     * @return boolean true if enrolment number already exists among saved, else false
     */
    private boolean doesFacultyIdAlreadyExists(FacultyMember facultyMember) {
        boolean exists = UserRegistrationUtils.userIdExists(facultyMember.getFacultyId(), userIds);
        if (exists) conditions.setDuplicateFacultyId(true);
        return exists;
    }
}
