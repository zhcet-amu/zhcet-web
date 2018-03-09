package amu.zhcet.core.error;

import amu.zhcet.auth.UserAuth;
import amu.zhcet.notification.Notification;
import amu.zhcet.notification.NotificationNotFoundException;
import amu.zhcet.notification.recipient.NotificationRecipient;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.CourseNotFoundException;
import amu.zhcet.data.department.Department;
import amu.zhcet.data.department.DepartmentNotFoundException;
import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserNotFoundException;
import amu.zhcet.data.user.faculty.FacultyMember;
import amu.zhcet.data.user.faculty.FacultyMemberNotFoundException;
import amu.zhcet.data.user.student.Student;
import amu.zhcet.data.user.student.StudentNotFoundException;

public class ErrorUtils {

    public static void requireNonNullDepartment(Department department) {
        if (department == null) throw new DepartmentNotFoundException();
    }

    public static void requireNonNullCourse(Course course) {
        if (course == null) throw new CourseNotFoundException();
    }

    public static void requireNonNullStudent(Student student) {
        if (student == null) throw new StudentNotFoundException();
    }

    public static void requireNonNullNotification(Notification notification) {
        if (notification == null) throw new NotificationNotFoundException();
    }

    public static void requireNonNullNotification(NotificationRecipient notification) {
        if (notification == null) throw new NotificationNotFoundException();
    }

    public static void requireNonNullUser(User user) {
        if (user == null) throw new UserNotFoundException();
    }

    public static void requireNonNullUser(UserAuth user) {
        if (user == null) throw new UserNotFoundException();
    }

    public static void requireNonNullFacultyMember(FacultyMember facultyMember) {
        if (facultyMember == null) throw new FacultyMemberNotFoundException();
    }
}
