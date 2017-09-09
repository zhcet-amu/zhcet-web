package in.ac.amu.zhcet.data.service;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.cloud.StorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class FirebaseService {

    public FirebaseService() throws IOException {
        log.info("Initializing Firebase");
        InputStream serviceAccount = getServiceAccountJson();

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                .setDatabaseUrl("https://zhcet-web-amu.firebaseio.com/")
                .setStorageBucket("zhcet-web-amu.appspot.com")
                .build();

        try {
            FirebaseApp.initializeApp(options);
            log.info("Firebase Initialized");
        } catch (RuntimeException i) {
            log.info("Firebase already Initialized");
        }
    }

    private InputStream getServiceAccountJson() {
        try {
            return new FileInputStream("service-account.json");
        } catch (FileNotFoundException e) {
            log.info("service-account.json not found in file system... Attempting to load from environment...");
            return new ByteArrayInputStream(System.getenv("FIREBASE_JSON").getBytes());
        }
    }

    private Bucket getBucket() {
        return StorageClient.getInstance().bucket();
    }

    public String uploadFile(String path, String contentType, InputStream fileStream) throws UnsupportedEncodingException {
        log.info(String.format("Uploading file '%s' of type %s...", path, contentType));
        Bucket bucket = getBucket();
        log.info("Bucket used : " + bucket.getName());
        String uuid = UUID.randomUUID().toString();
        log.info("Firebase Download Token : " + uuid);
        Map<String, String> map = new HashMap<>();
        map.put("firebaseStorageDownloadTokens", uuid);

        BlobInfo uploadContent = BlobInfo.newBuilder(getBucket().getName(), path)
                .setContentType(contentType)
                .setMetadata(map)
                .setAcl(Collections.singletonList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER)))
                .build();
        BlobInfo uploaded = bucket.getStorage().create(uploadContent, fileStream);

        log.info("File Uploaded");
        log.info("Media Link : " + uploaded.getMediaLink());
        log.info("Metadata : " + uploaded.getMetadata().toString());

        String link = String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media", uploaded.getBucket(), URLEncoder.encode(uploaded.getName(), "UTF-8"));
        log.info("Firebase Link : " + link);

        return link;
    }

}
