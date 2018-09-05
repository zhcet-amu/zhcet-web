package amu.zhcet.auth.verification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
class EmailVerificationService {

    /**
     * This helper is needed to call Async method for sending emails, and thus wraps all other calls
     */
    private final EmailVerificationHelper emailVerificationHelper;

    @Autowired
    public EmailVerificationService(EmailVerificationHelper emailVerificationHelper) {
        this.emailVerificationHelper = emailVerificationHelper;
    }

    /**
     * Generates the verification token and sends the email to the specified user
     *
     * If any user with the same email exists, throws a {@link DuplicateEmailException}
     *
     * @param email String email for which the verification token is to be generated
     */
    public void generate(String email) {
        emailVerificationHelper.sendMail(emailVerificationHelper.createTokenForEmail(email));
    }

    /**
     * Validated a verification token and invalidates it by setting as used already and saves in the database
     * Then sets the new email to the user account and sets it to be verified and publishes an event stating same
     * @param token String token ID
     */
    public void verifyEmail(String token) {
        emailVerificationHelper.verifyEmail(token);
    }

}
