package in.ac.amu.zhcet.service.notification.email.data;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class LinkMessage {
    private String recipient;
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

        if (recipient == null)
            invalidFields.add("recipient");
        if (subject == null)
            invalidFields.add("subject");

        if (!invalidFields.isEmpty())
            throw new InvalidMessageException("LinkMessage", invalidFields);
    }
}
