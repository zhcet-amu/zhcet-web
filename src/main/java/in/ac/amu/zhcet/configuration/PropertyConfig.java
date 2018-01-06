package in.ac.amu.zhcet.configuration;

import com.google.common.base.Strings;
import in.ac.amu.zhcet.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class PropertyConfig {

    private static Boolean PEPPER_SET;

    @Autowired
    PropertyConfig(ApplicationProperties applicationProperties) {
        if (!Strings.isNullOrEmpty(applicationProperties.getPepper()) && !applicationProperties.getPepper().equals(SecurityUtils.PEPPER)) {
            SecurityUtils.PEPPER = applicationProperties.getPepper();
            log.info("Applied pepper to application");
            PEPPER_SET = true;
        } else {
            log.error("Using default pepper for app, this is dangerous and can lead to hacking into system");
            PEPPER_SET = false;
        }
    }

    public static Boolean isPepperSet() {
        return PEPPER_SET;
    }
}
