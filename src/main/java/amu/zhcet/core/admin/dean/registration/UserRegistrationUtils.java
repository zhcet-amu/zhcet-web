package amu.zhcet.core.admin.dean.registration;

import amu.zhcet.data.department.Department;
import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserRegistrationUtils {

    /**
     * Checks if a department exists by name in the provided departments
     * And sets the department in user if it exists
     * @param user User in which department information exists and found department is to be set
     * @param departments List of departments (Search Space)
     * @return boolean true if department is found, false if not
     */
    public static boolean departmentExists(User user, List<Department> departments) {
        String departmentName = user.getDepartment().getName();

        Optional<Department> departmentOptional = departments.stream()
                .filter(department -> department.getName().equals(departmentName))
                .findFirst();

        if (!departmentOptional.isPresent()) {
            return false;
        } else {
            user.setDepartment(departmentOptional.get());
            return true;
        }
    }

    /**
     * Checks if a user ID exists in a list of ID identifiers
     * @param userId String user ID to be checked
     * @param userIds List of identifiers (Search Space)
     * @return boolean true of ID is found, false if not
     */
    public static boolean userIdExists(String userId, List<UserRepository.Identifier> userIds) {
        return userIds.stream()
                .anyMatch(identifier -> identifier.getUserId().equals(userId));
    }

}
