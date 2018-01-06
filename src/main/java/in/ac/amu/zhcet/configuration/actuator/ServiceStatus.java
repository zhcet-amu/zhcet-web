package in.ac.amu.zhcet.configuration.actuator;

import in.ac.amu.zhcet.configuration.ApplicationProperties;
import in.ac.amu.zhcet.configuration.EmailConfiguration;
import in.ac.amu.zhcet.configuration.PropertyConfig;
import in.ac.amu.zhcet.service.firebase.FirebaseService;
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
            public Boolean hasMessageServerKey;
        }

        public Boolean pepperSet;
        public Email email = new Email();
        public Firebase firebase = new Firebase();
    }

    private final ApplicationProperties applicationProperties;
    private final FirebaseService firebaseService;

    @Autowired
    public ServiceStatus(ApplicationProperties applicationProperties, FirebaseService firebaseService) {
        this.applicationProperties = applicationProperties;
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

        status.email.enabled = !applicationProperties.getEmail().isDisabled();
        status.email.working = EmailConfiguration.isEmailSet();

        status.firebase.enabled = !firebaseService.isDisabled();
        status.firebase.initialized = !firebaseService.isUninitialized();
        status.firebase.hasMessageServerKey = firebaseService.hasMessagingServerKey();

        status.pepperSet = PropertyConfig.isPepperSet();

        return status;
    }

}
