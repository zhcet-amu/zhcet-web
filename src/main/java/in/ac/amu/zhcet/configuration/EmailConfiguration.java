package in.ac.amu.zhcet.configuration;

import com.google.common.base.Strings;
import in.ac.amu.zhcet.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.Collections;
import java.util.Properties;

@Slf4j
@Configuration
public class EmailConfiguration {

    private static Boolean EMAIL_ENABLED;

    @Bean
    @Primary
    public JavaMailSender getJavaMailSender(ApplicationProperties applicationProperties) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        String username = applicationProperties.getEmail().getAddress();
        String password = applicationProperties.getEmail().getPassword();

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
