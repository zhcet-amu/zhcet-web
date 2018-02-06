package amu.zhcet.common.actuator;

import amu.zhcet.auth.login.LoginAttemptService;
import amu.zhcet.core.ViewService;
import amu.zhcet.email.EmailProperties;
import amu.zhcet.firebase.FirebaseService;
import org.modelmapper.ModelMapper;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ActuatorBeans {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnEnabledEndpoint
    public LoggedInEndoint loggedInEndpoint(ModelMapper modelMapper, ViewService viewService) {
        return new LoggedInEndoint(modelMapper, viewService);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnEnabledEndpoint
    public BlockedEndpoint blockedIpsEndpoint(LoginAttemptService loginAttemptService) {
        return new BlockedEndpoint(loginAttemptService);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnEnabledEndpoint
    public ServiceStatusEndpoint serviceStatusEndpoint(EmailProperties emailProperties, FirebaseService firebaseService) {
        return new ServiceStatusEndpoint(emailProperties, firebaseService);
    }

}
