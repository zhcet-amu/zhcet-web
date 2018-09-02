package amu.zhcet.auth.verification;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
public class RecentVerificationException extends RuntimeException {

    public static final String MESSAGE = "Verification link was recently sent. Please wait for some time";

    @Nullable
    private LocalDateTime sentTime;

    public RecentVerificationException() {
        super(MESSAGE);
    }

    public RecentVerificationException(LocalDateTime sentTime) {
        super(String.format(
                "Verification link was recently sent at %s. Please wait for some time",
                sentTime.toString()));
        this.sentTime = sentTime;
    }

}
