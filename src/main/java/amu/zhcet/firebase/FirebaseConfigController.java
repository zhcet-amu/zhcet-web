package amu.zhcet.firebase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FirebaseConfigController {

    private final FirebaseProperties firebaseProperties;

    @Autowired
    public FirebaseConfigController(FirebaseProperties firebaseProperties) {
        this.firebaseProperties = firebaseProperties;
    }

    @Cacheable("firebase-config")
    @GetMapping("/firebase:config.js")
    public String config() {
        return firebaseProperties.getConfig();
    }

}
