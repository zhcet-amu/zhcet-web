package amu.zhcet.storage.image.upload;

import amu.zhcet.common.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
class UploadedImage extends BaseEntity {
    @Id
    private String id;
    private String url;
    private String filename;
    private String originalFilename;
    private String contentType;
    private boolean isThumbnail;
    private String user;
}
