package amu.zhcet.firebase;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class FirebaseConfigController {

    private final FirebaseProperties firebaseProperties;
    private final TemplateEngine templateEngine;

    private Context firebaseContext;

    @Autowired
    public FirebaseConfigController(FirebaseProperties firebaseProperties,
                                    @Qualifier("extraTemplateEngine") TemplateEngine templateEngine) {
        this.firebaseProperties = firebaseProperties;
        this.templateEngine = templateEngine;
    }

    private Context getFirebaseContext() {
        if (firebaseContext == null) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("config", firebaseProperties.getConfig());
            firebaseContext = new Context(Locale.getDefault(), payload);
        }

        return firebaseContext;
    }

    @Cacheable("firebase-config")
    @GetMapping("/js/firebase-config.js")
    public ResponseEntity<String> firebaseConfig() {
        String rendered = templateEngine.process("js/firebase-config", getFirebaseContext());

        return ResponseEntity.accepted()
                .contentType(MediaType.parseMediaType("text/javascript"))
                .cacheControl(CacheControl
                        .maxAge(365, TimeUnit.DAYS)
                        .sMaxAge(365, TimeUnit.DAYS)
                        .cachePublic())
                .body(rendered);
    }

    @Cacheable("firebase-config")
    @GetMapping("/firebase-messaging-sw.js")
    public ResponseEntity<String> firebaseMessagingSw() {
        String rendered = templateEngine.process("js/firebase-messaging-sw", getFirebaseContext());

        return ResponseEntity.accepted()
                .contentType(MediaType.parseMediaType("text/javascript"))
                .cacheControl(CacheControl
                        .maxAge(1, TimeUnit.DAYS)
                        .sMaxAge(1, TimeUnit.DAYS))
                .body(rendered);
    }

}
