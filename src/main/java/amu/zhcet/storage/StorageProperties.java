package amu.zhcet.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Paths;

@Data
@ConfigurationProperties("zhcet.storage")
public class StorageProperties {

    private String rootDir = "app-root";
    private String uploadDir = "uploads";
    private final Csv csv = new Csv();
    private final Image image = new Image();
    private final Document document = new Document();

    @Data
    public class Csv {
        private String location = "csv";

        public String getLocation() {
            return Paths.get(getUploadDir(), location).toString();
        }
    }

    @Data
    public class Image {
        private String location = "img";

        public String getLocation() {
            return Paths.get(getUploadDir(), location).toString();
        }
    }

    @Data
    public class Document {
        private String location = "doc";

        public String getLocation() {
            return Paths.get(getUploadDir(), location).toString();
        }
    }

    public String getUploadDir() {
        return Paths.get(rootDir, uploadDir).toString();
    }

}