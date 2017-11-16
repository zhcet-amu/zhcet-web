package in.ac.amu.zhcet.service.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import in.ac.amu.zhcet.configuration.ApplicationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;

@Slf4j
@Service
public class FirebaseService {

    private static final boolean DEBUG_SUPPRESS = false;
    private final String messagingServerKey;
    private boolean uninitialized;

    @Autowired
    public FirebaseService(ApplicationProperties applicationProperties) throws IOException {
        log.info("Initializing Firebase");
        ApplicationProperties.Firebase firebase = applicationProperties.getFirebase();
        messagingServerKey = firebase.getMessagingServerKey();
        InputStream serviceAccount = getServiceAccountJson();

        if (serviceAccount == null) {
            log.error("Firebase Service Account JSON not found anywhere. Any Firebase interaction may throw exception");
            uninitialized = true;
            return;
        }

        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(serviceAccount);
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

    private InputStream getServiceAccountJson() {
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
            return is;
        } catch (FileNotFoundException e) {
            log.warn("service-account.json not found in storage system... Attempting to load from environment...");
            String property = System.getenv("FIREBASE_JSON");
            if (property == null)
                return null;
            return new ByteArrayInputStream(System.getenv("FIREBASE_JSON").getBytes());
        }
    }

    public Bucket getBucket() {
        return StorageClient.getInstance().bucket();
    }

    public boolean canProceed() {
        boolean unproceedable = uninitialized || DEBUG_SUPPRESS;
        if (unproceedable)
            log.error("Cannot proceed as Firebase is uninitialized");

        return !unproceedable;
    }

    public boolean canSendMessage() {
        boolean unsendable = messagingServerKey == null || DEBUG_SUPPRESS;
        if (unsendable)
            log.error("Cannot broadcast as Firebase Messaging Server Key is not found");

        return !unsendable;
    }

    public String getMessagingServerKey() {
        return messagingServerKey;
    }

}
