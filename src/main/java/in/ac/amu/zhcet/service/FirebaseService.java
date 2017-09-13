package in.ac.amu.zhcet.service;

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
import java.net.URL;
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
            return is;
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

        String link = String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media&token=%s",
                uploaded.getBucket(), URLEncoder.encode(uploaded.getName(), "UTF-8"), uuid);
        log.info("Firebase Link : " + link);

        return link;
    }

}
