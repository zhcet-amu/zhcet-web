package amu.zhcet.core.admin.faculty.attendance.upload;

import amu.zhcet.data.course.registration.CourseRegistration;
import lombok.Data;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class AttendanceUploadIntegrityVerifier {

    @Data
    static class ErrorConditions {
        private boolean notRegistered;
        private boolean duplicateStudent;
    }

    private final ErrorConditions conditions = new ErrorConditions();
    private final List<CourseRegistration> registrations;

    private final Map<String, Boolean> usedEnrolmentNumbers = new HashMap<>();

    AttendanceUploadIntegrityVerifier(List<CourseRegistration> registrations) {
        Assert.notNull(registrations, "Registrations should not be null");
        this.registrations = registrations;
    }

    public String getError(AttendanceUpload attendanceUpload) {
        Assert.notNull(attendanceUpload, "Attendance Upload should not be null");

        if (isDuplicateEntry(attendanceUpload.getEnrolmentNo())) {
            return "Duplicate Student Found";
        } else if (!isStudentRegistered(attendanceUpload.getEnrolmentNo())) {
            return "Student not registered";
        } else {
            return null;
        }
    }

    public ErrorConditions getErrorConditions() {
        return conditions;
    }

    private boolean isDuplicateEntry(String key) {
        if (usedEnrolmentNumbers.containsKey(key)) {
            conditions.setDuplicateStudent(true);
            return true;
        }

        usedEnrolmentNumbers.put(key, true);
        return false;
    }

    private boolean isStudentRegistered(String enrolmentNo) {
        boolean exists = registrations.stream()
                .map(registration -> registration.getStudent().getEnrolmentNumber())
                .anyMatch(enrolment -> enrolment.equals(enrolmentNo));

        if (!exists) conditions.setNotRegistered(true);
        return exists;
    }

}
