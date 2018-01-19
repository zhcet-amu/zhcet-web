package amu.zhcet.storage.image;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "bytes")
public class Image {
    private String name;
    private String extension;
    private String contentType;
    private byte[] bytes;
    private boolean isThumbnail;
}
