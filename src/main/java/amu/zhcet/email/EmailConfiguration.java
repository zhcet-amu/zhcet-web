package amu.zhcet.email;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Slf4j
@Configuration
public class EmailConfiguration {

    private static Boolean EMAIL_ENABLED;

    @Bean
    @Primary
    public JavaMailSender getJavaMailSender(EmailProperties emailProperties) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        String username = emailProperties.getAddress();
        String password = emailProperties.getPassword();

        if (Strings.isNullOrEmpty(username) || Strings.isNullOrEmpty(password)) {
            log.error("CONFIG (Email): Email or Password not found : {} {}", username, password);
            EMAIL_ENABLED = false;
        } else {
            EMAIL_ENABLED = true;
        }

        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }

    public static Boolean isEmailSet() {
        return EMAIL_ENABLED;
    }

}
