package amu.zhcet.storage.image.upload;

import org.springframework.data.jpa.repository.JpaRepository;

interface UploadedImageRepository extends JpaRepository<UploadedImage, String> {
}
