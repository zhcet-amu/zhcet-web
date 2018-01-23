package amu.zhcet.core.shared.course.registration.upload;

import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.registration.CourseRegistration;
import amu.zhcet.data.user.student.Student;
import lombok.Data;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CourseRegistrationIntegrityVerifier {

    @Data
    static class ErrorConditions {
        private boolean invalidEnrolment;
        private boolean alreadyEnrolled;
        private boolean duplicateFacultyNo;
    }

    private final ErrorConditions conditions = new ErrorConditions();
    private final Course course;
    private final List<CourseRegistration> registrations;

    private final Map<String, Boolean> usedFacultyNumbers = new HashMap<>();

    CourseRegistrationIntegrityVerifier(Course course, List<CourseRegistration> registrations) {
        Assert.notNull(course, "Course should not be null");
        Assert.notNull(registrations, "Course Registrations should not be null");

        this.course = course;
        this.registrations = registrations;
    }

    public String getError(Student student) {
        Assert.notNull(student, "Student should not be null");

        if (isDuplicateStudent(student)) {
            return "Duplicate Faculty Number";
        } else if (!isValidStudent(student)) {
            return  "No such student found";
        } else if(isStudentAlreadyEnrolled(student)) {
            return "Already enrolled in " + course.getCode();
        } else {
            return null;
        }
    }

    public ErrorConditions getErrorConditions() {
        return conditions;
    }

    private boolean isDuplicateStudent(Student student) {
        String key = student.getFacultyNumber();
        if (usedFacultyNumbers.containsKey(key)) {
            conditions.setDuplicateFacultyNo(true);
            return true;
        }

        usedFacultyNumbers.put(key, true);
        return false;
    }

    private boolean isStudentAlreadyEnrolled(Student student) {
        boolean enrolled = registrations.stream()
                .map(CourseRegistration::getStudent)
                .anyMatch(oldStudent -> oldStudent.equals(student));

        if (enrolled) conditions.setAlreadyEnrolled(true);
        return enrolled;
    }

    private boolean isValidStudent(Student student) {
        if (student.getEnrolmentNumber() == null || student.getFacultyNumber() == null) {
            conditions.setInvalidEnrolment(true);
            return false;
        }

        return true;
    }
}
