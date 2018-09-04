package amu.zhcet.auth.verification;

import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserService;
import amu.zhcet.email.LinkMailService;
import amu.zhcet.email.LinkMessage;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class EmailVerificationHelper {

    private final UserService userService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final Cache<String, LocalDateTime> emailCache;
    private final LinkMailService linkMailService;

    @Autowired
    public EmailVerificationHelper(
            UserService userService,
            VerificationTokenRepository verificationTokenRepository,
            ApplicationEventPublisher eventPublisher,
            LinkMailService linkMailService
    ) {
        this.userService = userService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.eventPublisher = eventPublisher;
        this.linkMailService = linkMailService;

        this.emailCache = Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(Duration.of(10, ChronoUnit.MINUTES))
                .build();
    }

    /**
     * Generates the verification token and sends the email to the specified user
     *
     * If any user with the same email exists, throws a {@link DuplicateEmailException}
     *
     * @param email String email for which the verification token is to be generated
     */
    public VerificationToken createTokenForEmail(String email) {
        User loggedInUser = userService.getLoggedInUser()
                .orElseThrow(() -> new IllegalStateException("No user logged in"));
        Optional<User> userOptional = userService.getUserByEmail(email)
                .filter(user -> !user.getUserId().equals(loggedInUser.getUserId()));

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.isEmailVerified()) {
                // Another user is present which has the same verified email
                // Throw exception notifying about the situation
                throw new DuplicateEmailException(email);
            } else {
                // The user has not yet verified his/her email and yet another user
                // asked for the same email, this may happen when the first user was
                // mischievous and just trying emails but couldn't verify, we will
                // transfer the email to current user

                log.warn("User {} has requested email {} which was requested already by {}",
                        loggedInUser, email, user);
                log.info("Resetting {} email and transferring to {}", user, loggedInUser);

                user.setEmail(null);
                eventPublisher.publishEvent(new DuplicateEmailEvent(loggedInUser, loggedInUser, email));
                userService.save(user);
            }
        }

        return createVerificationToken(email);
    }

    /**
     * Validated a verification token and invalidates it by setting as used already and saves in the database
     * Then sets the new email to the user account and sets it to be verified and publishes an event stating same
     * @param token String token ID
     */
    public void verifyEmail(String token) {
        VerificationToken verificationToken = getAndValidateOrThrow(token);

        verificationToken.setUsed(true);
        verificationTokenRepository.save(verificationToken);

        String email = verificationToken.getEmail();
        User user = verificationToken.getUser();

        Optional<User> userOptional = userService.getUserByEmail(email)
                .filter(us -> !us.getUserId().equals(user.getUserId()));

        if (userOptional.isPresent()) {
            // Some other user has the same email as in this token, this should not happen
            // But this is possible in the scenario of a verification lag. If that user is verified,
            // we will throw an exception and if that user is not verified, we will favour this user
            // and transfer the email from him/her to this user

            User duplicateEmailUser = userOptional.get();
            if (duplicateEmailUser.isEmailVerified()) {
                throw new DuplicateEmailException(email);
            } else {
                log.warn("User {} verified it's email {} owned by another user {}", user, email, duplicateEmailUser);
                duplicateEmailUser.setEmail(null);
                userService.save(user);
                eventPublisher.publishEvent(new DuplicateEmailEvent(user, duplicateEmailUser, email));
            }
        }

        user.setEmail(email);
        user.setEmailVerified(true);
        userService.save(user);
        eventPublisher.publishEvent(new EmailVerifiedEvent(user));
    }

    /**
     * Sends mail to the user to be verified and saves the user with the email and status to be unverified
     * Also, publishes an event notifying the same
     * @param token {@link VerificationToken} token to be sent email with respect to
     */
    @Async
    public void sendMail(VerificationToken token) {
        String relativeUrl = "/login/email/verify?auth=" + token.getToken();
        log.debug("Verification link generated : {}", relativeUrl);

        try {
            linkMailService.sendEmail(getPayLoad(token.getEmail(), token.getUser(), relativeUrl), false);
            // Now we set the email to the user and disable email verified
            User user = token.getUser();
            log.debug("Saving user email {} -> {}", user.getUserId(), token.getEmail());
            user.setEmail(token.getEmail());
            user.setEmailVerified(false);

            userService.save(user);
            log.debug("Saved user email");
            eventPublisher.publishEvent(new EmailVerifiedEvent(user));
        } catch (MailException mailException) {
            // There was an error sending the mail, so remove the token and sent time
            verificationTokenRepository.delete(token);
            emailCache.invalidate(token.getUser().getUserId());
            log.warn("Email sending for {} '{}' failed, so we removed the verification token",
                    token.getUser(), token.getEmail(), mailException);
        }
    }

    /**
     * Retrieves the verification token from database and validates it by performing checks
     * that it exists, is not already used and not expired. If any of the validation fails,
     * throws an {@link TokenVerificationException} with the corresponding message
     * @param token String token ID
     * @return {@link VerificationToken}
     */
    private VerificationToken getAndValidateOrThrow(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

        if (verificationToken == null)
            throw new TokenVerificationException("Token: " + token + " is invalid");

        if (verificationToken.isUsed())
            throw new TokenVerificationException("Token: " + token + " is already used! Please request another link!");

        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiry().getTime() - cal.getTime().getTime()) <= 0)
            throw new TokenVerificationException("Token: " + token + " has expired");

        return verificationToken;
    }

    private VerificationToken createVerificationToken(String email) {
        User user = userService.getLoggedInUser().orElseThrow(() -> new IllegalStateException("No user logged in"));
        // Check if link was already sent
        LocalDateTime sentTime = emailCache.getIfPresent(user.getUserId());
        if (sentTime != null)
            throw new RecentVerificationException(sentTime);

        emailCache.put(user.getUserId(), LocalDateTime.now());

        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken(user, token, email);
        verificationTokenRepository.save(verificationToken);

        return verificationToken;
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

}
