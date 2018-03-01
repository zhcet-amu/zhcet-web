package amu.zhcet.email;

import amu.zhcet.common.utils.ConsoleHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Slf4j
@Configuration
public class EmailConfiguration {

    private static Boolean EMAIL_ENABLED;

    @Autowired(required = false)
    public EmailConfiguration(JavaMailSender javaMailSender) {
        if (javaMailSender == null) {
            log.error(ConsoleHelper.red("CONFIG (Email): Email or Password not found"));
            EMAIL_ENABLED = false;
        } else {
            EMAIL_ENABLED = true;
            log.info(ConsoleHelper.green("Email Initialized"));
        }
    }

    public static Boolean isEmailSet() {
        return EMAIL_ENABLED;
    }

}
