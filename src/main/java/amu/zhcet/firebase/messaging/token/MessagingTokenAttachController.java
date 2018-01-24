package amu.zhcet.firebase.messaging.token;

import amu.zhcet.auth.Auditor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessagingTokenAttachController {

    private final MessagingTokenAttachService messagingTokenAttachService;

    public MessagingTokenAttachController(MessagingTokenAttachService messagingTokenAttachService) {
        this.messagingTokenAttachService = messagingTokenAttachService;
    }

    @PostMapping("/profile/api/messaging_token")
    public String postMessagingToken(@RequestBody String token) {
        messagingTokenAttachService.attachToken(Auditor.getLoggedInUsername(), token);
        return "OK";
    }

}
