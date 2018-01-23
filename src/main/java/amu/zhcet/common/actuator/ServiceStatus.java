package amu.zhcet.common.actuator;

import amu.zhcet.email.EmailConfiguration;
import amu.zhcet.email.EmailProperties;
import amu.zhcet.firebase.FirebaseService;
import amu.zhcet.security.SecurePropertyConfig;
import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.stereotype.Component;

@Component
public class ServiceStatus implements Endpoint<ServiceStatus.Status> {

    @Getter
    @ToString
    static class Status {
        @Getter
        @ToString
        static class Email {
            public Boolean enabled;
            public Boolean working;
        }

        @Getter
        @ToString
        static class Firebase {
            public Boolean enabled;
            public Boolean initialized;
            public Boolean proceedable;
        }

        public Boolean pepperSet;
        public Email email = new Email();
        public Firebase firebase = new Firebase();
    }

    private final EmailProperties emailProperties;
    private final FirebaseService firebaseService;

    @Autowired
    public ServiceStatus(EmailProperties emailProperties, FirebaseService firebaseService) {
        this.emailProperties = emailProperties;
        this.firebaseService = firebaseService;
    }

    @Override
    public String getId() {
        return "service-status";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isSensitive() {
        return true;
    }

    @Override
    public Status invoke() {
        Status status = new Status();

        status.email.enabled = !emailProperties.isDisabled();
        status.email.working = EmailConfiguration.isEmailSet();

        status.firebase.enabled = firebaseService.isEnabled();
        status.firebase.initialized = firebaseService.isInitialized();
        status.firebase.proceedable = firebaseService.canProceed();

        status.pepperSet = SecurePropertyConfig.isPepperSet();

        return status;
    }

}
