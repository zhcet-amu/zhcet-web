package amu.zhcet.security.permission;

import amu.zhcet.core.auth.CustomUser;
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

    public boolean hasPermission(Collection<? extends GrantedAuthority> authorities, String permission) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(permission));
    }

    public boolean hasPermission(Collection<? extends GrantedAuthority> authorities, Role role) {
        return hasPermission(authorities, role.toString());
    }

    public boolean checkDepartment(Authentication user, String departmentCode) {
        if (hasPermission(user.getAuthorities(), Role.DEPARTMENT_SUPER_ADMIN))
            return true;

        if (!(user.getPrincipal() instanceof CustomUser))
            return false;
        return hasPermission(user.getAuthorities(), Role.DEPARTMENT_ADMIN) &&
                ((CustomUser) user.getPrincipal()).getDepartment().getCode().equals(departmentCode);
    }

    public boolean checkCourse(Authentication user, String departmentCode, String courseCode) {
        Course course = courseService.getCourse(courseCode);
        if (course == null) // If course isn't found, leave it to Controller
            return checkDepartment(user, departmentCode);
        else
            return checkDepartment(user, departmentCode) && course.getDepartment().getCode().equals(departmentCode);
    }

    public boolean checkNotificationCreator(Authentication user, String notificationId) {
        boolean hasSendingPermission = hasPermission(user.getAuthorities(), Role.TEACHING_STAFF) ||
                hasPermission(user.getAuthorities(), Role.DEVELOPMENT_ADMIN);
        try {
            Notification notification = notificationRepository.findOne(Long.parseLong(notificationId));
            if (notification == null) // If notification isn't found, leave it to Controller
                return hasSendingPermission;
            else
                return hasSendingPermission && notification.getSender().getUserId().equals(user.getName());
        } catch (NumberFormatException nfe) {
            return true;
        }
    }

    public boolean checkNotificationRecipient(Authentication user, String notificationId) {
        try {
            NotificationRecipient notification = notificationRecipientRepository.findOne(Long.parseLong(notificationId));
            // If notification is not found, leave it to Controller
            return notification == null || notification.getRecipient().getUserId().equals(user.getName());
        } catch (NumberFormatException nfe) {
            return true;
        }
    }

}
