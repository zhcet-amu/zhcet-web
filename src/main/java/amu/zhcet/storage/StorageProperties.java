package amu.zhcet.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("storage")
public class StorageProperties {

    private final Csv csv = new Csv();
    private final Image image = new Image();
    private final Document document = new Document();

    @Data
    public static class Csv {
        private String location = "app-root/upload-dir/csv";
    }

    @Data
    public static class Image {
        private String location = "app-root/upload-dir/img";
    }

    @Data
    public static class Document {
        private String location = "app-root/upload-dir/doc";
    }

}