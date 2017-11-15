package in.ac.amu.zhcet.service.notification.email;

import in.ac.amu.zhcet.service.ConfigurationService;
import in.ac.amu.zhcet.service.markdown.MarkDownService;
import in.ac.amu.zhcet.service.notification.email.data.LinkMessage;
import in.ac.amu.zhcet.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
public class LinkMailService {

    private static final String LINK_TEMPLATE = "html/link";

    private final EmailService emailService;
    private final ConfigurationService configurationService;
    private final TemplateEngine htmlTemplateEngine;
    private final MarkDownService markDownService;

    public LinkMailService(EmailService emailService, ConfigurationService configurationService, @Qualifier("emailEngine") TemplateEngine htmlTemplateEngine, MarkDownService markDownService) {
        this.emailService = emailService;
        this.configurationService = configurationService;
        this.htmlTemplateEngine = htmlTemplateEngine;
        this.markDownService = markDownService;
    }

    private String normalizeUrl(String url) {
        return configurationService.getBaseUrl() + url;
    }

    private String getUnSubscribeUrl(String recipient) {
        return String.format("%s/login/unsubscribe?email=%s&conf=%s",
                configurationService.getBaseUrl(), recipient, SecurityUtils.getHash(recipient));
    }

    private String render(String template, Map<String, Object> payload) {
        return htmlTemplateEngine.process(template, new Context(Locale.getDefault(), payload));
    }

    private void normalizeMessage(LinkMessage linkMessage) {
        linkMessage.validateSelf();

        if (linkMessage.getLink() == null && linkMessage.getRelativeLink() != null)
            linkMessage.setLink(normalizeUrl(linkMessage.getRelativeLink()));

        if (linkMessage.isMarkdown()) {
            linkMessage.setTitle(markDownService.render(linkMessage.getTitle()));
            linkMessage.setPreMessage(markDownService.render(linkMessage.getPreMessage()));
            linkMessage.setPostMessage(markDownService.render(linkMessage.getPostMessage()));
        }
    }

    private String getHtml(LinkMessage linkMessage) {
        Map<String, Object> payLoad = new HashMap<>();

        payLoad.put("link_message", linkMessage);

        String html = render(LINK_TEMPLATE, payLoad);
        Path path = Paths.get("test.html");
        byte[] strToBytes = html.getBytes();

        try {
            Files.write(path, strToBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return html;
    }

    public void sendEmail(LinkMessage linkMessage, boolean allowUnSubscribe) {
        normalizeMessage(linkMessage);

        if (allowUnSubscribe) {
            String unSubscribeUrl = getUnSubscribeUrl(linkMessage.getRecipientEmail());
            log.info("Adding Un-Subscribe Link {}", unSubscribeUrl);

            linkMessage.setUnSubscribeUrl(unSubscribeUrl);
        }

        log.info("Sending Email: {}", linkMessage);
        emailService.sendHtmlMail(linkMessage.getRecipientEmail(), linkMessage.getSubject(), getHtml(linkMessage));
    }

    public void sendEmail(LinkMessage linkMessage) {
        sendEmail(linkMessage, true);
    }

}
