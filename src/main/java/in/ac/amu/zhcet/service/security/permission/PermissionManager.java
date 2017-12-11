package in.ac.amu.zhcet.service.security.permission;

import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.notification.Notification;
import in.ac.amu.zhcet.data.model.notification.NotificationRecipient;
import in.ac.amu.zhcet.data.repository.NotificationRecipientRepository;
import in.ac.amu.zhcet.data.repository.NotificationRepository;
import in.ac.amu.zhcet.data.type.Roles;
import in.ac.amu.zhcet.service.CourseManagementService;
import in.ac.amu.zhcet.service.user.CustomUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class PermissionManager {

    private final CourseManagementService courseManagementService;
    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;

    @Autowired
    public PermissionManager(CourseManagementService courseManagementService, NotificationRepository notificationRepository, NotificationRecipientRepository notificationRecipientRepository) {
        this.courseManagementService = courseManagementService;
        this.notificationRepository = notificationRepository;
        this.notificationRecipientRepository = notificationRecipientRepository;
    }

    public static List<GrantedAuthority> authorities(List<String> roles) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        // Add normal stored roles
        for (String role: roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role));
            grantedAuthorities.addAll(getExtraPermissions(role));
        }

        return grantedAuthorities;
    }

    private static List<GrantedAuthority> getExtraPermissions(String role) {
        List<GrantedAuthority> permissions = new ArrayList<>();

        switch (role) {
            case Roles.SUPER_ADMIN:
                permissions.add(new SimpleGrantedAuthority(Roles.DEAN_ADMIN));
                permissions.add(new SimpleGrantedAuthority(Roles.MANAGEMENT_ADMIN));
                permissions.add(new SimpleGrantedAuthority(Roles.DEPARTMENT_SUPER_ADMIN));
                permissions.add(new SimpleGrantedAuthority(Roles.DEPARTMENT_ADMIN));
                permissions.add(new SimpleGrantedAuthority(Roles.SUPER_FACULTY));
                permissions.add(new SimpleGrantedAuthority(Roles.FACULTY));
                break;
            case Roles.DEPARTMENT_SUPER_ADMIN:
                permissions.add(new SimpleGrantedAuthority(Roles.DEPARTMENT_ADMIN));
                break;
            case Roles.SUPER_FACULTY:
                permissions.add(new SimpleGrantedAuthority(Roles.FACULTY));
                break;
        }

        return permissions;
    }

    public boolean hasPermission(Collection<? extends GrantedAuthority> authorities, String permission) {
        return authorities.stream().map(GrantedAuthority::getAuthority).anyMatch(authority -> authority.equals(permission));
    }

    public boolean checkDepartment(Authentication user, String departmentCode) {
        if (hasPermission(user.getAuthorities(), Roles.DEPARTMENT_SUPER_ADMIN))
            return true;

        if (!(user.getPrincipal() instanceof CustomUser))
            return false;
        return hasPermission(user.getAuthorities(), Roles.DEPARTMENT_ADMIN) &&
                ((CustomUser) user.getPrincipal()).getDepartment().getCode().equals(departmentCode);
    }

    public boolean checkCourse(Authentication user, String departmentCode, String courseCode) {
        Course course = courseManagementService.getCourse(courseCode);
        return checkDepartment(user, departmentCode) && (course == null || course.getDepartment().getCode().equals(departmentCode));
    }

    public boolean checkNotificationCreator(Authentication user, String notificationId) {
        boolean hasSendingPermission = hasPermission(user.getAuthorities(), Roles.DEAN_ADMIN) ||
                hasPermission(user.getAuthorities(), Roles.DEPARTMENT_ADMIN) ||
                hasPermission(user.getAuthorities(), Roles.FACULTY) ||
                hasPermission(user.getAuthorities(), Roles.MANAGEMENT_ADMIN);
        try {
            Notification notification = notificationRepository.findOne(Long.parseLong(notificationId));
            return notification != null && hasSendingPermission && notification.getSender().getUserId().equals(user.getName());
        } catch (NumberFormatException nfe) {
            return true;
        }
    }

    public boolean checkNotificationRecipient(Authentication user, String notificationId) {
        try {
            NotificationRecipient notification = notificationRecipientRepository.findOne(Long.parseLong(notificationId));
            return notification != null && notification.getRecipient().getUserId().equals(user.getName());
        } catch (NumberFormatException nfe) {
            return true;
        }
    }

}
