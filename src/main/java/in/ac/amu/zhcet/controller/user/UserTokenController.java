package in.ac.amu.zhcet.controller.user;

import com.google.firebase.auth.FirebaseAuth;
import in.ac.amu.zhcet.service.user.Auditor;
import in.ac.amu.zhcet.service.user.CustomUser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
public class UserTokenController {

    private static final UserInformation UNAUTHENTICATED = new UserInformation();

    private final ModelMapper modelMapper;

    @Autowired
    public UserTokenController(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Data
    private static class UserInformation {
        private String token;
        private String username;
        private String name;
        private String avatar;
        private String type;
        private String departmentName;
        private boolean authenticated;
    }

    private UserInformation fromUser(CustomUser user, String token) {
        if (user == null) {
            return UNAUTHENTICATED;
        }

        UserInformation information = modelMapper.map(user, UserInformation.class);
        information.setToken(token);
        information.setAuthenticated(true);
        return information;
    }

    @GetMapping("/profile/api/token")
    public UserInformation getToken() {
        try {
            CustomUser user = Auditor.getLoggedInUser();
            Map<String, Object> claims = new HashMap<>();
            claims.put("type", user.getType());
            claims.put("department", user.getDepartment().getName());
            String token = FirebaseAuth.getInstance().createCustomTokenAsync(user.getUsername(), claims).get();
            return fromUser(user, token);
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

}
