package amu.zhcet.core.dean.edit;

import amu.zhcet.common.error.DuplicateException;
import amu.zhcet.common.error.InvalidEmailException;
import amu.zhcet.common.utils.Utils;
import amu.zhcet.data.department.Department;
import amu.zhcet.data.department.DepartmentNotFoundException;
import amu.zhcet.data.user.User;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
class ModelEditUtils {

    /**
     * Checks user with duplicate or valid email and throws appropriate exceptions
     * Returns email to be set on user after sanitizing it
     * @param userSupplier Provides user to be saved
     * @param newEmail Provides new email to be set
     * @param duplicateChecker Checks if any other user with same email exists
     * @return Email to be set
     */
    public static String verifyNewEmail(
            Supplier<User> userSupplier,
            Supplier<String> newEmail,
            BiPredicate<User, String> duplicateChecker) {
        User user = userSupplier.get();

        // Sanitize the email from user input
        String email = Strings.emptyToNull(newEmail.get().trim().toLowerCase());
        String previousEmail = user.getEmail();

        // Update email address if not null and changed
        if (email != null && !email.equals(previousEmail)) {
            if (!Utils.isValidEmail(email))
                throw new InvalidEmailException(email);
            if (duplicateChecker.test(user, email))
                throw new DuplicateException("User", "email", email);

            // New email means we should remove the flag denoting the email
            // has been verified
            user.setEmailVerified(false);
        }

        return email;
    }

    /**
     * Verifies if a department exists and returns
     * Throws exception if department is not found
     * @param departmentName Name of department to be searched
     * @param departmentOptional Function taking department name as input and returning Department
     * @return Department if found
     */
    public static Department verifyDepartment(String departmentName, Function<String, Optional<Department>> departmentOptional) {
        return departmentOptional.apply(departmentName).orElseThrow(() -> {
            log.warn("Tried saving with non-existent department {}", departmentName);
            return new DepartmentNotFoundException(departmentName);
        });
    }

}
