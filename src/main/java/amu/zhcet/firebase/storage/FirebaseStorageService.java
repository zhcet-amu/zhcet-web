package amu.zhcet.firebase.storage;

import amu.zhcet.firebase.FirebaseService;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class FirebaseStorageService {

    private final FirebaseService firebaseService;

    @Autowired
    public FirebaseStorageService(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    /**
     * Uploads a file to Firebase Storage
     * @param path Path at which the file is to be stored on Firebase
     * @param contentType Content-Type of file - Helps Firebase to set meta data on file
     * @param content byte array of file to be stored
     * @return Public facing URL of the uploaded file
     * @throws UnsupportedEncodingException if the file type is unsupported
     */
    public CompletableFuture<Optional<String>> uploadFile(String path, String contentType, byte[] content) throws UnsupportedEncodingException {
        if (!firebaseService.canProceed())
            return CompletableFuture.completedFuture(Optional.empty());

        log.info("Uploading file '{}' of type {}...", path, contentType);
        Bucket bucket = firebaseService.getBucket();
        log.info("Bucket used : " + bucket.getName());
        String uuid = UUID.randomUUID().toString();
        log.info("Firebase Download Token : {}", uuid);
        Map<String, String> map = new HashMap<>();
        map.put("firebaseStorageDownloadTokens", uuid);

        BlobInfo uploadContent = BlobInfo.newBuilder(bucket.getName(), path)
                .setContentType(contentType)
                .setMetadata(map)
                .setAcl(Collections.singletonList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER)))
                .build();
        BlobInfo uploaded = bucket.getStorage().create(uploadContent, content);

        log.info("File Uploaded");
        log.info("Media Link : {}", uploaded.getMediaLink());
        log.info("Metadata : {}", uploaded.getMetadata().toString());

        String link = String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media&auth=%s",
                uploaded.getBucket(), URLEncoder.encode(uploaded.getName(), "UTF-8"), uuid);
        log.info("Firebase Link : {}", link);

        return CompletableFuture.completedFuture(Optional.of(link));
    }

}
