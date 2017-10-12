package in.ac.amu.zhcet.service.misc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
public class EmailService{
    private final JavaMailSender sender;
    private final TemplateEngine htmlTemplateEngine;

    @Value("${email}")
    private String senderEmail;

    @Autowired
    public EmailService(JavaMailSender sender, @Qualifier("emailEngine") TemplateEngine htmlTemplateEngine) {
        this.sender = sender;
        this.htmlTemplateEngine = htmlTemplateEngine;
    }

    private static void setBasicInfo(MimeMessageHelper mailMessage, String from, String to, String subject, String[] bcc) throws MessagingException {
        mailMessage.setSubject(subject);
        mailMessage.setFrom(from);
        mailMessage.setTo(to);
        if (bcc != null)
            mailMessage.setBcc(bcc);
    }

    private MimeMessage constructEmail(String emailAddress, String subject, String body, String[] bcc) throws MessagingException {
        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setText(body);
        setBasicInfo(helper, senderEmail, emailAddress, subject, bcc);
        return mimeMessage;
    }

    private MimeMessage constructHtmlEmail(String emailAddress, String subject, String body, String[] bcc) throws MessagingException {
        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setText(body, true);
        setBasicInfo(helper, senderEmail, emailAddress, subject, bcc);
        return mimeMessage;
    }

    public void sendHtmlMail(String email, String subject, String html) {
        try {
            sender.send(constructHtmlEmail(email, subject, html, null));
        } catch (MessagingException e) {
            log.error("Error sending mail", e);
        }
    }

    public void sendHtmlMail(String email, String subject, String html, String[] bcc) {
        try {
            sender.send(constructHtmlEmail(email, subject, html, bcc));
        } catch (MessagingException e) {
            log.error("Error sending mail", e);
        }
    }

    public void sendSimpleMail(String email, String subject, String message) {
        try {
            sender.send(constructEmail(email, subject, message, null));
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public String render(String template, Map<String, Object> payload) {
        return htmlTemplateEngine.process(template, new Context(Locale.getDefault(), payload));
    }

}
