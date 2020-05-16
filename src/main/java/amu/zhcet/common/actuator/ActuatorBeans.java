package amu.zhcet.common.actuator;

import amu.zhcet.auth.AuthService;
import amu.zhcet.auth.login.LoginAttemptService;
import amu.zhcet.email.EmailProperties;
import amu.zhcet.firebase.FirebaseService;
import org.modelmapper.ModelMapper;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ActuatorBeans {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint
    public LoggedInEndoint loggedInEndpoint(ModelMapper modelMapper, AuthService authService) {
        return new LoggedInEndoint(modelMapper, authService);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint
    public BlockedEndpoint blockedIpsEndpoint(LoginAttemptService loginAttemptService) {
        return new BlockedEndpoint(loginAttemptService);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint
    public ServiceStatusEndpoint serviceStatusEndpoint(EmailProperties emailProperties, FirebaseService firebaseService) {
        return new ServiceStatusEndpoint(emailProperties, firebaseService);
    }

}
