package in.ac.amu.zhcet.service.misc;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.cloud.StorageClient;
import in.ac.amu.zhcet.configuration.ApplicationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class FirebaseService {

    @Autowired
    public FirebaseService(ApplicationProperties applicationProperties) throws IOException {
        log.info("Initializing Firebase");
        ApplicationProperties.Firebase firebase = applicationProperties.getFirebase();
        InputStream serviceAccount = getServiceAccountJson();

        if (serviceAccount == null) {
            log.error("Firebase Service Account JSON not found anywhere. Any Firebase interaction will throw exception");
            return;
        }

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
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

    private Bucket getBucket() {
        return StorageClient.getInstance().bucket();
    }

    public String uploadFile(String path, String contentType, InputStream fileStream) throws UnsupportedEncodingException {
        log.warn("Uploading file '{}' of type {}...", path, contentType);
        Bucket bucket = getBucket();
        log.warn("Bucket used : " + bucket.getName());
        String uuid = UUID.randomUUID().toString();
        log.warn("Firebase Download Token : {}", uuid);
        Map<String, String> map = new HashMap<>();
        map.put("firebaseStorageDownloadTokens", uuid);

        BlobInfo uploadContent = BlobInfo.newBuilder(getBucket().getName(), path)
                .setContentType(contentType)
                .setMetadata(map)
                .setAcl(Collections.singletonList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER)))
                .build();
        BlobInfo uploaded = bucket.getStorage().create(uploadContent, fileStream);

        log.warn("File Uploaded");
        log.warn("Media Link : {}", uploaded.getMediaLink());
        log.warn("Metadata : {}", uploaded.getMetadata().toString());

        String link = String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media&auth=%s",
                uploaded.getBucket(), URLEncoder.encode(uploaded.getName(), "UTF-8"), uuid);
        log.warn("Firebase Link : {}", link);

        return link;
    }

}
