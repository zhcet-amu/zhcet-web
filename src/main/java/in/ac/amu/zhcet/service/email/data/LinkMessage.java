package in.ac.amu.zhcet.service.email.data;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkMessage {
    private String recipientEmail;
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
    private boolean markdown;

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
