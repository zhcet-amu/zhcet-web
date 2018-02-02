package amu.zhcet.firebase;

import amu.zhcet.common.utils.ConsoleHelper;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.cloud.StorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class FirebaseService {

    private final FirebaseLocator firebaseLocator;

    private final boolean disabled;
    private final String firebaseConfig;
    private boolean uninitialized;

    private GoogleCredential googleCredential;
    private String projectId;

    @Autowired
    public FirebaseService(FirebaseLocator firebaseLocator, FirebaseProperties firebase) throws IOException {
        this.firebaseLocator = firebaseLocator;
        log.info(ConsoleHelper.blue("Initializing Firebase"));
        disabled = firebase.isDisabled();
        firebaseConfig = firebase.getConfig();

        if (disabled) {
            log.warn(ConsoleHelper.red("CONFIG (Firebase): Firebase is disabled"));
            return;
        }

        if (firebaseLocator.found()) {
            initializeFirebase();
        } else {
            log.warn(ConsoleHelper.red("CONFIG (Firebase): Firebase Service Account JSON not found anywhere. Any Firebase interaction may throw exception"));
            uninitialized = true;
        }
    }

    public static FirebaseToken getToken(String token) throws ExecutionException, InterruptedException {
        return FirebaseAuth.getInstance().verifyIdTokenAsync(token).get();
    }

    public String getConfig() {
        return firebaseConfig;
    }

    public String getStorageBucket() {
        return projectId + ".appspot.com";
    }

    public String getDatabaseUrl() {
        return "https://" + projectId + ".firebaseio.com/";
    }

    public String getMessagingServer() {
        return "https://fcm.googleapis.com/v1/projects/" + projectId + "/messages:send";
    }

    public Bucket getBucket() {
        return StorageClient.getInstance().bucket();
    }

    public String getToken() {
        try {
            googleCredential.refreshToken();
        } catch (IOException e) {
            log.error("Error refreshing token", e);
        }
        return googleCredential.getAccessToken();
    }

    public boolean canProceed() {
        boolean proceedable = isInitialized() && isEnabled();
        if (!proceedable)
            log.info("Cannot proceed as Firebase is uninitialized");

        return proceedable;
    }

    public boolean isEnabled() {
        return !disabled;
    }

    public boolean isInitialized() {
        return !uninitialized;
    }

    private void initializeFirebase() throws IOException {
        try {
            String messagingScope = "https://www.googleapis.com/auth/firebase.messaging";
            googleCredential = GoogleCredential.fromStream(firebaseLocator.getServiceAccountStream())
                    .createScoped(Collections.singletonList(messagingScope));
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(firebaseLocator.getServiceAccountStream());

            projectId = ((ServiceAccountCredentials) googleCredentials).getProjectId();

            if (projectId == null)
                throw new RuntimeException("Project ID must not be null");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(googleCredentials)
                    .setDatabaseUrl(getDatabaseUrl())
                    .setStorageBucket(getStorageBucket())
                    .build();

            FirebaseApp.initializeApp(options);
            log.info(ConsoleHelper.green("Firebase Initialized"));
        } catch (IllegalStateException ise) {
            log.info(ConsoleHelper.yellow("Firebase already initialized"));
        } catch (IllegalArgumentException e) {
            uninitialized = true;
            log.error("Firebase couldn't be initialized", e);
        }
    }

}
