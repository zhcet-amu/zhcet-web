package amu.zhcet.security;

import amu.zhcet.common.utils.ConsoleHelper;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SecurePropertyConfig {

    private static Boolean PEPPER_SET;

    @Autowired
    SecurePropertyConfig(SecureProperties secureProperties) {
        String pepper = secureProperties.getPepper();
        if (!Strings.isNullOrEmpty(pepper) && !pepper.equals(SecurityUtils.getPepper())) {
            SecurityUtils.setPepper(pepper);
            log.info(ConsoleHelper.green("Applied pepper to application"));
            PEPPER_SET = true;
        } else {
            log.error(ConsoleHelper.red("Using default pepper for app, this is dangerous and can lead to hacking into system"));
            PEPPER_SET = false;
        }
    }

    public static Boolean isPepperSet() {
        return PEPPER_SET;
    }
}
