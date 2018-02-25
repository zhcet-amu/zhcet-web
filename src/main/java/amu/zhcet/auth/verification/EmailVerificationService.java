package amu.zhcet.auth.verification;

import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserService;
import amu.zhcet.email.LinkMailService;
import amu.zhcet.email.LinkMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
class EmailVerificationService {

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
        User user = userService.getLoggedInUser().orElseThrow(() -> new IllegalStateException("No user logged in"));
        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken(user, token, email);
        verificationTokenRepository.save(verificationToken);

        return verificationToken;
    }

    public VerificationToken generate(String email) {
        Optional<User> userOptional = userService.getUserByEmail(email);
        if (userOptional.isPresent() && !userOptional.get().getEmail().equals(userService.getLoggedInUser()
                .orElseThrow(() -> new IllegalStateException("No user logged in")).getEmail()))
            throw new DuplicateEmailException(email);

        return createVerificationToken(email);
    }

    public String validate(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

        if (verificationToken == null)
            return "Token: " + token + " is invalid";

        if (verificationToken.isUsed())
            return "Token: " + token + " is already used! Please request another link!";

        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiry().getTime() - cal.getTime().getTime()) <= 0) {
            return "Token: " + token + " has expired";
        }

        return null;
    }

    public void confirmEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.setUsed(true);
        verificationTokenRepository.save(verificationToken);

        User user = verificationToken.getUser();
        user.setEmail(verificationToken.getEmail());
        user.setEmailVerified(true);
        userService.save(user);
    }

    private LinkMessage getPayLoad(String recipientEmail, User user, String url) {
        return LinkMessage.builder()
                .recipientEmail(recipientEmail)
                .title("Email Verification")
                .subject("ZHCET Email Verification")
                .name(user.getName())
                .relativeLink(url)
                .linkText("Verify Account")
                .preMessage("You need to verify your email for user ID: " + user.getUserId() +
                        "<br>Please click the button below to verify your email and account")
                .postMessage("If you did not request the password reset, please contact website admin")
                .build();
    }

    public void sendMail(VerificationToken token) {
        String relativeUrl = "/login/verify?auth=" + token.getToken();
        log.debug("Verification link generated : {}", relativeUrl);

        linkMailService.sendEmail(getPayLoad(token.getEmail(), token.getUser(), relativeUrl), false);
    }

}
