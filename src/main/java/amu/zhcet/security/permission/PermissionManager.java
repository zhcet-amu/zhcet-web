package amu.zhcet.security.permission;

import amu.zhcet.auth.UserAuth;
import amu.zhcet.core.notification.Notification;
import amu.zhcet.core.notification.NotificationRepository;
import amu.zhcet.core.notification.recipient.NotificationRecipient;
import amu.zhcet.core.notification.recipient.NotificationRecipientRepository;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.CourseService;
import amu.zhcet.data.course.floated.FloatedCourseService;
import amu.zhcet.data.user.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

@Slf4j
@Service
public class PermissionManager {

    private final RoleHierarchy roleHierarchy;
    private final CourseService courseService;
    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;

    @Autowired
    public PermissionManager(RoleHierarchy roleHierarchy, CourseService courseService, FloatedCourseService floatedCourseService, NotificationRepository notificationRepository, NotificationRecipientRepository notificationRecipientRepository) {
        this.roleHierarchy = roleHierarchy;
        this.courseService = courseService;
        this.notificationRepository = notificationRepository;
        this.notificationRecipientRepository = notificationRecipientRepository;
    }

    public List<GrantedAuthority> authorities(List<String> roles) {
        return roles.stream()
                .flatMap(role -> roleHierarchy.getReachableGrantedAuthorities(createAuthorityList(role)).stream())
                .collect(Collectors.toList());
    }

    public static boolean hasPermission(Collection<? extends GrantedAuthority> authorities, String permission) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(permission));
    }

    public static boolean hasPermission(Collection<? extends GrantedAuthority> authorities, Role role) {
        return hasPermission(authorities, role.toString());
    }

    public boolean checkDepartment(Authentication user, String departmentCode) {
        if (hasPermission(user.getAuthorities(), Role.DEPARTMENT_SUPER_ADMIN))
            return true;

        if (!(user.getPrincipal() instanceof UserAuth))
            return false;

        UserAuth userAuth = (UserAuth) user.getPrincipal();

        boolean isDepartmentAdmin = hasPermission(user.getAuthorities(), Role.DEPARTMENT_ADMIN);

        if (departmentCode == null) {
            return isDepartmentAdmin;
        } else {
            return isDepartmentAdmin && userAuth.getDepartment().getCode().equals(departmentCode);
        }
    }

    public boolean checkCourse(Authentication user, String courseCode) {
        Optional<Course> courseOptional = courseService.getCourse(courseCode);
        // If course isn't found, leave it to Controller
        return courseOptional.map(course -> checkDepartment(user, course.getDepartment().getCode())).orElse(checkDepartment(user, null));
    }

    public boolean checkNotificationCreator(Authentication user, String notificationId) {
        boolean hasSendingPermission = hasPermission(user.getAuthorities(), Role.TEACHING_STAFF) ||
                hasPermission(user.getAuthorities(), Role.DEVELOPMENT_ADMIN);
        try {
            Optional<Notification> notificationOptional = notificationRepository.findById(Long.parseLong(notificationId));
            // If notification isn't found, leave it to Controller
            return notificationOptional.map(notification -> hasSendingPermission && notification.getSender().getUserId().equals(user.getName())).orElse(hasSendingPermission);
        } catch (NumberFormatException nfe) {
            return true;
        }
    }

    public boolean checkNotificationRecipient(Authentication user, String notificationId) {
        try {
            Optional<NotificationRecipient> notificationOptional = notificationRecipientRepository.findById(Long.parseLong(notificationId));
            // If notification is not found, leave it to Controller
            return notificationOptional.map(notification -> notification.getRecipient().getUserId().equals(user.getName())).orElse(true);
        } catch (NumberFormatException nfe) {
            return true;
        }
    }

}
