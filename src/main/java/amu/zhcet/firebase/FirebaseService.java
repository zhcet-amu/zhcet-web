package amu.zhcet.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.common.base.Strings;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.cloud.StorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

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
            return;
        }

        if (Strings.isNullOrEmpty(messagingServerKey)) {
            log.warn("CONFIG (Firebase Messaging): Firebase Messaging Server Key not found!");
        }

        Optional<InputStream> serviceAccountOptional = getServiceAccountJson();
        if (!serviceAccountOptional.isPresent()) {
            log.warn("CONFIG (Firebase): Firebase Service Account JSON not found anywhere. Any Firebase interaction may throw exception");
            uninitialized = true;
            return;
        }

        try {
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(serviceAccountOptional.get());
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(googleCredentials)
                    .setDatabaseUrl(firebase.getDatabaseUrl())
                    .setStorageBucket(firebase.getStorageBucket())
                    .build();

            FirebaseApp.initializeApp(options);
            log.info("Firebase Initialized");
        } catch (IllegalStateException ise) {
            log.info("Firebase already initialized");
        } catch (IllegalArgumentException e) {
            uninitialized = true;
            log.error("Firebase couldn't be initialized", e);
        }

    }

    private Optional<InputStream> getServiceAccountJson() {
        String fileName = "service-account.json";
        try {
            InputStream is = getClass().getResourceAsStream("/" + fileName);
            if (is == null) {
                log.info("service-account.json not found in class resources. Maybe debug build? Trying to load another way");
                URL url = getClass().getClassLoader().getResource(fileName);

                if (url == null) {
                    log.info(fileName + " not found in class loader resource as well... Using last resort...");
                    throw new FileNotFoundException();
                }

                is = new FileInputStream(url.getFile());
            }
            return Optional.of(is);
        } catch (FileNotFoundException e) {
            log.info("service-account.json not found in storage system... Attempting to load from environment...");
            String property = System.getenv("FIREBASE_JSON");
            if (property == null) {
                log.warn("FIREBASE account.json not found anywhere!");
                return Optional.empty();
            }
            return Optional.of(new ByteArrayInputStream(System.getenv("FIREBASE_JSON").getBytes()));
        }
    }

    public Bucket getBucket() {
        return StorageClient.getInstance().bucket();
    }

    public static FirebaseToken getToken(String token) throws ExecutionException, InterruptedException {
        return FirebaseAuth.getInstance().verifyIdTokenAsync(token).get();
    }

    public boolean canProceed() {
        boolean proceedable = !isUninitialized() && !isDisabled();
        if (!proceedable)
            log.info("Cannot proceed as Firebase is uninitialized");

        return proceedable;
    }

    public boolean canSendMessage() {
        boolean unsendable = !hasMessagingServerKey() || isDisabled();
        if (unsendable)
            log.info("Cannot broadcast as Firebase Messaging Server Key is not found");

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
