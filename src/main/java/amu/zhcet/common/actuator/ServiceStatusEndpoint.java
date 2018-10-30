package amu.zhcet.common.actuator;

import amu.zhcet.email.EmailConfiguration;
import amu.zhcet.email.EmailProperties;
import amu.zhcet.firebase.FirebaseService;
import amu.zhcet.security.SecurePropertyConfig;
import lombok.Getter;
import lombok.ToString;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

@Endpoint(id = "servicestatus")
public class ServiceStatusEndpoint {

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

    public ServiceStatusEndpoint(EmailProperties emailProperties, FirebaseService firebaseService) {
        this.emailProperties = emailProperties;
        this.firebaseService = firebaseService;
    }

    @ReadOperation
    public Status getStatus() {
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
