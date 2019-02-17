package amu.zhcet.email;

import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Builder(buildMethodName = "ignore")
@NoArgsConstructor
@AllArgsConstructor
public class LinkMessage {
    private String recipientEmail;
    @Setter(value = AccessLevel.NONE)
    private String[] bcc;
    private String name;
    private String subject;
    private String title;
    private String relativeLink;
    private String link;
    private String linkText;
    private String preMessage;
    private String postMessage;
    private String unSubscribeUrl;
    private String preview;

    public static class LinkMessageBuilder {

        public LinkMessageBuilder bcc(List<String> bcc) {
            if (bcc == null) {
                this.bcc = null;
            } else {
                this.bcc = bcc.toArray(new String[bcc.size()]);
            }
            return this;
        }

        public LinkMessageBuilder bcc(String... bcc) {
            this.bcc = bcc;
            return this;
        }

        public LinkMessage build() {
            LinkMessage linkMessage = new LinkMessage(
                    recipientEmail,
                    bcc,
                    name,
                    subject,
                    title,
                    relativeLink,
                    link,
                    linkText,
                    preMessage,
                    postMessage,
                    unSubscribeUrl,
                    preview
            );

            linkMessage.normalizeBcc();
            return linkMessage;
        }
    }

    public void setBcc(String[] bcc) {
        this.bcc = bcc;
        normalizeBcc();
    }

    public void setBcc(List<String> bcc) {
        if (bcc == null) {
            setBcc((String[]) null);
            return;
        }
        setBcc(bcc.toArray(new String[bcc.size()]));
    }

    private void normalizeBcc() {
        if (bcc == null)
            return;

        if (bcc.length > 0 && this.recipientEmail == null) {
            this.recipientEmail = bcc[0];
            this.bcc = Arrays.copyOfRange(bcc, 1, bcc.length);
        }

        if (bcc.length == 0) {
            this.bcc = null;
        }
    }

    public static LinkMessageBuilder builder() {
        return new LinkMessageBuilder();
    }

    public void validateSelf() {
        List<String> invalidFields = new ArrayList<>();

        if (recipientEmail == null)
            invalidFields.add("recipientEmail");
        if (subject == null)
            invalidFields.add("subject");

        if (!invalidFields.isEmpty())
            throw new InvalidMessageException("LinkMessage", invalidFields);
    }
}
