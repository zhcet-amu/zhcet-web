package amu.zhcet.auth;

import amu.zhcet.data.user.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Special helper service for common authorization related methods
 * Methods are present as both static and instance methods as instance methods
 * are used in Thymeleaf Templates
 */
@Service
public class AuthService {

    public static boolean isFullyAuthenticated(User user) {
        return user.isPasswordChanged() && user.isEmailVerified() && user.getEmail() != null;
    }

    public static List<String> getPendingTasks(User user) {
        List<String> pendingTasks = new ArrayList<>();
        if (user.getEmail() == null)
            pendingTasks.add("Register your email");
        if (!user.isEmailVerified())
            pendingTasks.add("Verify your email");
        if (!user.isPasswordChanged())
            pendingTasks.add("Change your password from default one");
        return pendingTasks;
    }

}
