package amu.zhcet.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.common.base.Strings;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.util.Optional;

@Slf4j
@Service
public class FirebaseService {

    private final boolean disabled;
    private final String messagingServerKey;
    private boolean uninitialized;

    @Autowired
    public FirebaseService(FirebaseProperties firebase) throws IOException {
        log.info("Initializing Firebase");
        disabled = firebase.isDisabled();
        messagingServerKey = firebase.getMessagingServerKey();

        if (disabled) {
            log.warn("CONFIG (Firebase): Firebase is disabled");
        }

        if (Strings.isNullOrEmpty(messagingServerKey)) {
            log.error("CONFIG (Firebase Messaging): Firebase Messaging Server Key not found!");
        }

        Optional<InputStream> serviceAccountOptional = getServiceAccountJson();
        if (!serviceAccountOptional.isPresent()) {
            log.error("CONFIG (Firebase): Firebase Service Account JSON not found anywhere. Any Firebase interaction may throw exception");
            uninitialized = true;
            return;
        }

        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(serviceAccountOptional.get());
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(googleCredentials)
                .setDatabaseUrl(firebase.getDatabaseUrl())
                .setStorageBucket(firebase.getStorageBucket())
                .build();

        try {
            FirebaseApp.initializeApp(options);
            log.info("Firebase Initialized");
        } catch (IllegalStateException ise) {
            log.warn("Firebase already initialized");
        }
    }

    private Optional<InputStream> getServiceAccountJson() {
        String fileName = "service-account.json";
        try {
            InputStream is = getClass().getResourceAsStream("/" + fileName);
            if (is == null) {
                log.warn("service-account.json not found in class resources. Maybe debug build? Trying to load another way");
                URL url = getClass().getClassLoader().getResource(fileName);

                if (url == null) {
                    log.warn(fileName + " not found in class loader resource as well... Using last resort...");
                    throw new FileNotFoundException();
                }

                is = new FileInputStream(url.getFile());
            }
            return Optional.of(is);
        } catch (FileNotFoundException e) {
            log.warn("service-account.json not found in storage system... Attempting to load from environment...");
            String property = System.getenv("FIREBASE_JSON");
            if (property == null)
                return Optional.empty();
            return Optional.of(new ByteArrayInputStream(System.getenv("FIREBASE_JSON").getBytes()));
        }
    }

    public Bucket getBucket() {
        return StorageClient.getInstance().bucket();
    }

    public boolean canProceed() {
        boolean unproceedable = isUninitialized() || isDisabled();
        if (unproceedable)
            log.error("Cannot proceed as Firebase is uninitialized");

        return !unproceedable;
    }

    public boolean canSendMessage() {
        boolean unsendable = !hasMessagingServerKey() || isDisabled();
        if (unsendable)
            log.error("Cannot broadcast as Firebase Messaging Server Key is not found");

        return !unsendable;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public boolean isUninitialized() {
        return uninitialized;
    }

    public boolean hasMessagingServerKey() {
        return getMessagingServerKey() != null;
    }

    public String getMessagingServerKey() {
        return messagingServerKey;
    }

}
