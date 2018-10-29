package amu.zhcet.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class ServiceWorkerController {

    private final TemplateEngine templateEngine;

    @Autowired
    public ServiceWorkerController(@Qualifier("extraTemplateEngine") TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Cacheable("sw")
    @GetMapping("/sw.js")
    public ResponseEntity<String> serviceWorker() {
        ClassPathResource classPathResource = new ClassPathResource("src/sw.mjs");
        String loaded = null;
        try {
            loaded = IOUtils.toString(classPathResource.getInputStream(), "UTF-8");
        } catch (IOException e) {
            log.error("Error loading service worker", e);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/javascript"))
                .cacheControl(CacheControl
                        .maxAge(0, TimeUnit.SECONDS))
                .body(loaded);
    }

}
