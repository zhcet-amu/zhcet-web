package in.ac.amu.zhcet.data.service.token;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService{
    private final JavaMailSender sender;

    @Value("${email}")
    private String senderEmail;

    @Autowired
    public EmailService(JavaMailSender sender) {
        this.sender = sender;
    }

    private SimpleMailMessage constructEmail(String emailAddress, String subject, String body) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(emailAddress);
        email.setFrom(senderEmail);
        return email;
    }

    public void sendMail(String email, String subject, String message) {
        sender.send(constructEmail(email, subject, message));
    }

}
