package amu.zhcet.firebase;

import amu.zhcet.common.utils.ConsoleHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

@Slf4j
@Service
class FirebaseLocator {

    private final String serviceAccountJson;

    @Autowired
    public FirebaseLocator(FirebaseProperties properties) {
        serviceAccountJson = locateServiceAccount(properties);
    }

    public boolean found() {
        return serviceAccountJson != null;
    }

    public String getServiceAccountJson() {
        return serviceAccountJson;
    }

    public InputStream getServiceAccountStream() {
        if (!found())
            return null;
        return IOUtils.toInputStream(serviceAccountJson, Charset.defaultCharset());
    }

    private String locateServiceAccount(FirebaseProperties properties) {
        String filename = "service-account.json";
        String json = properties.getServiceAccount();

        if (json == null) {
            log.info(filename + " not found in Properties. Trying resources");
            json = getFromResources(filename);
        }

        if (json == null) {
            log.info(filename + " not found in class resources. Maybe debug build? Trying to load another way");
            json = getFromClassLoader(filename);
        }

        if (json == null) {
            log.info(filename + " not found in class loader resource as well... Using last resort...");
            json = getFromEnvironment();
        }

        if (json == null)
            log.warn(ConsoleHelper.red("FIREBASE account.json not found anywhere!"));

        return json;
    }

    private String getFromClassLoader(String filename) {
        URL url = getClass().getClassLoader().getResource(filename);

        if (url == null)
            return null;

        try {
            return IOUtils.toString(url, Charset.defaultCharset());
        } catch (IOException e) {
            return null;
        }
    }

    private String getFromResources(String filename) {
        return getFromInputStream(getClass().getResourceAsStream("/" + filename));
    }

    private static String getFromInputStream(InputStream inputStream) {
        if (inputStream == null)
            return null;

        try {
            return IOUtils.toString(inputStream, Charset.defaultCharset());
        } catch (IOException e) {
            return null;
        }
    }

    private static String getFromEnvironment() {
        log.info("service-account.json not found in storage system... Attempting to load from environment...");
        return System.getenv("FIREBASE_JSON");
    }
}
