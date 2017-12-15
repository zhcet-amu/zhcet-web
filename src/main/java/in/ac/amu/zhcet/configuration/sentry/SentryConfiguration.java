package in.ac.amu.zhcet.configuration.sentry;

import in.ac.amu.zhcet.service.UserService;
import io.sentry.Sentry;
import io.sentry.event.helper.EventBuilderHelper;
import io.sentry.spring.SentryExceptionResolver;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Optional;

@Slf4j
@Component
public class SentryConfiguration {

    @Data
    private static class UserContext {
        private String userId;
        private String name;
        private String departmentName;
        private String email;
        private String[] roles;
        private String type;
    }

    private static final UserContext UNAUTHORIZED = new UserContext();

    static {
        UNAUTHORIZED.setUserId("UNAUTHORIZED");
    }

    @Autowired
    public SentryConfiguration(UserService userService, ModelMapper modelMapper) {
        EventBuilderHelper myEventBuilderHelper = eventBuilder -> {
            UserContext userContext = userService.getLoggedInUser()
                    .flatMap(user -> Optional.of(modelMapper.map(user, UserContext.class)))
                    .orElse(UNAUTHORIZED);

            eventBuilder.withExtra("user", userContext);
        };

        Sentry.getStoredClient().addBuilderHelper(myEventBuilderHelper);
    }

    @Bean
    public HandlerExceptionResolver sentryExceptionResolver() {
        return new SentryExceptionResolver();
    }

    @Bean
    public ServletContextInitializer sentryServletContextInitializer() {
        return new io.sentry.spring.SentryServletContextInitializer();
    }

}
