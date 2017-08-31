package in.ac.amu.zhcet.data.service;

import in.ac.amu.zhcet.data.model.base.user.UserAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService{
    private final JavaMailSender sender;

    @Autowired
    public EmailService(JavaMailSender sender) {
        this.sender = sender;
    }

    private SimpleMailMessage constructResetTokenEmail(String contextPath, String token, UserAuth userAuth) {
        String url = contextPath + "/login/reset_password?id=" + userAuth.getUserId() + "&token=" + token;
        log.info("Password reset link generated : " + url);
        String message = "You requested password reset on zhcet for user ID: " + userAuth.getUserId() + "\r\n" +
                "Please click this link to reset password. \r\n" + url +
                "\r\nIf you did not request the password reset, please contact admin";
        return constructEmail("ZHCET Reset Password Link", message, userAuth);
    }

    private SimpleMailMessage constructEmail(String subject, String body, UserAuth userAuth) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(userAuth.getEmail());
        email.setFrom(System.getenv("ZHCET_WEB_EMAIL"));
        return email;
    }

    public void sendMail(String contextPath, String token, UserAuth userAuth){
        sender.send(constructResetTokenEmail( contextPath,  token,  userAuth));
    }

}
