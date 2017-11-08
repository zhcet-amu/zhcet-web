package in.ac.amu.zhcet.service.user.auth;

import in.ac.amu.zhcet.data.model.token.VerificationToken;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.data.repository.VerificationTokenRepository;
import in.ac.amu.zhcet.service.notification.EmailService;
import in.ac.amu.zhcet.service.ConfigurationService;
import in.ac.amu.zhcet.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class EmailVerificationService {

    private final ConfigurationService configurationService;
    private final UserService userService;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;

    @Autowired
    public EmailVerificationService(ConfigurationService configurationService, UserService userService, EmailService emailService, VerificationTokenRepository verificationTokenRepository) {
        this.configurationService = configurationService;
        this.userService = userService;
        this.emailService = emailService;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    private VerificationToken createVerificationToken(String email) {
        UserAuth user = userService.getLoggedInUser();
        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken(user, token, email);
        verificationTokenRepository.save(verificationToken);

        return verificationToken;
    }

    public VerificationToken generate(String email) {
        UserAuth userAuth = userService.getUserByEmail(email);
        if (userAuth != null && !userAuth.getEmail().equals(userService.getLoggedInUser().getEmail()))
            throw new DuplicateEmailException(email);

        return createVerificationToken(email);
    }

    public String validate(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

        if (verificationToken == null)
            return "Token: "+ token +" is invalid";

        if (verificationToken.isUsed())
            return "Token: "+ token +" is already used! Please request another link!";

        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiry().getTime() - cal.getTime().getTime()) <= 0) {
            return "Token: "+token+" has locked";
        }

        return null;
    }

    public void confirmEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.setUsed(true);
        verificationTokenRepository.save(verificationToken);

        UserAuth userAuth = verificationToken.getUser();
        userAuth.setEmail(verificationToken.getEmail());
        userAuth.setEmailVerified(true);
        userService.save(userAuth);
    }

    public void sendMail(VerificationToken token) {
        String url = configurationService.getBaseUrl() + "/login/verify?auth=" + token.getToken();

        Map<String, Object> map = new HashMap<>();
        map.put("title", "Email Verification");
        map.put("name", token.getUser().getName());
        map.put("link", url);
        map.put("link_text", "Verify Account");
        map.put("pre_message", "You need to verify your email for user ID: " + token.getUser().getUserId() +
                "<br>Please click the button below to verify your email and account");
        map.put("post_message", "If you did not request the password reset, please contact website admin");

        log.info("Verification link generated : " + url);
        String message = emailService.render("html/link", map);

        emailService.sendHtmlMail(token.getEmail(), "ZHCET Email Verification", message);
    }

}
