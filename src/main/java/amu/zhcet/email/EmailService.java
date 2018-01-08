package amu.zhcet.email;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

@Slf4j
@Service
public class EmailService{
    private final boolean disabled;

    private final JavaMailSender sender;
    private final String senderEmail;

    @Autowired
    public EmailService(JavaMailSender sender, EmailProperties emailProperties) {
        this.sender = sender;
        this.disabled = emailProperties.isDisabled();
        this.senderEmail = emailProperties.getAddress();

        if (disabled) {
            log.warn("CONFIG (Email): Email sending is disabled");
        }
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

    private void sendMail(MimeMessage mimeMessage) throws MessagingException {
        if (disabled) {
            try {
                log.warn("Skipping mail because of property override.\n" +
                        "Sender: {}\n" +
                        "Recipients: {}\n" +
                        "Subject: {}\n" +
                        "Content: {}",
                        mimeMessage.getSender(), mimeMessage.getAllRecipients(),
                        mimeMessage.getSubject(), mimeMessage.getContent());
            } catch (IOException e) {
                log.error("Error extracting information", e);
            }
            return;
        }

        if (Strings.isNullOrEmpty(senderEmail)) {
            log.error("Sender Email not configured. Skipping...");
        }

        sender.send(mimeMessage);
    }

    void sendHtmlMail(String email, String subject, String html, String[] bcc) {
        try {
            sendMail(constructHtmlEmail(email, subject, html, bcc));
        } catch (MessagingException e) {
            log.error("Error sending mail", e);
        }
    }

    void sendHtmlMail(String email, String subject, String html) {
        sendHtmlMail(email, subject, html, null);
    }

    public void sendSimpleMail(String email, String subject, String message) {
        try {
            sendMail(constructEmail(email, subject, message, null));
        } catch (MessagingException e) {
            log.error("Error sending mail", e);
        }
    }

}
