package in.ac.amu.zhcet.service.firebase.storage;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import in.ac.amu.zhcet.service.firebase.FirebaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class FirebaseStorageService {

    private final FirebaseService firebaseService;

    @Autowired
    public FirebaseStorageService(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    public String uploadFile(String path, String contentType, InputStream fileStream) throws UnsupportedEncodingException {
        if (!firebaseService.canProceed())
            return null;

        log.warn("Uploading file '{}' of type {}...", path, contentType);
        Bucket bucket = firebaseService.getBucket();
        log.warn("Bucket used : " + bucket.getName());
        String uuid = UUID.randomUUID().toString();
        log.warn("Firebase Download Token : {}", uuid);
        Map<String, String> map = new HashMap<>();
        map.put("firebaseStorageDownloadTokens", uuid);

        BlobInfo uploadContent = BlobInfo.newBuilder(bucket.getName(), path)
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
