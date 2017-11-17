package in.ac.amu.zhcet.service.user.auth;

import in.ac.amu.zhcet.data.model.token.VerificationToken;
import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.data.repository.VerificationTokenRepository;
import in.ac.amu.zhcet.service.UserService;
import in.ac.amu.zhcet.service.email.LinkMailService;
import in.ac.amu.zhcet.service.email.data.LinkMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.UUID;

@Slf4j
@Service
public class EmailVerificationService {

    private final UserService userService;
    private final LinkMailService linkMailService;
    private final VerificationTokenRepository verificationTokenRepository;

    @Autowired
    public EmailVerificationService(UserService userService, LinkMailService linkMailService, VerificationTokenRepository verificationTokenRepository) {
        this.userService = userService;
        this.linkMailService = linkMailService;
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

    private LinkMessage getPayLoad(String recipientEmail, UserAuth userAuth, String url) {
        return LinkMessage.builder()
                .recipientEmail(recipientEmail)
                .title("Email Verification")
                .subject("ZHCET Email Verification")
                .name(userAuth.getName())
                .relativeLink(url)
                .linkText("Verify Account")
                .preMessage("You need to verify your email for user ID: " + userAuth.getUserId() +
                        "<br>Please click the button below to verify your email and account")
                .postMessage("If you did not request the password reset, please contact website admin")
                .build();
    }

    public void sendMail(VerificationToken token) {
        String relativeUrl = "/login/verify?auth=" + token.getToken();
        log.info("Verification link generated : {}", relativeUrl);

        linkMailService.sendEmail(getPayLoad(token.getEmail(), token.getUser(), relativeUrl), false);
    }

}
