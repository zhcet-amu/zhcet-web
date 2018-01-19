package amu.zhcet.storage.image.upload;

import amu.zhcet.storage.image.Image;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.InputStream;

@Data
@EqualsAndHashCode(callSuper = false)
public class ImageUpload extends Image {
    private InputStream inputStream;
    private long size;
}
