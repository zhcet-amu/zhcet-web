package in.ac.amu.zhcet.controller.profile;

import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.service.FirebaseService;
import in.ac.amu.zhcet.service.core.UserService;
import in.ac.amu.zhcet.service.user.UserDetailService;
import in.ac.amu.zhcet.utils.ImageUtils;
import in.ac.amu.zhcet.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

@Slf4j
@Controller
public class AvatarService {

    private final UserService userService;
    private final UserDetailService userDetailService;
    private final FirebaseService firebaseService;

    @Autowired
    public AvatarService(UserService userService, UserDetailService userDetailService, FirebaseService firebaseService) {
        this.userService = userService;
        this.userDetailService = userDetailService;
        this.firebaseService = firebaseService;
    }

    private boolean verifyType(String fileName, boolean contentType) {
        if (fileName != null && !fileName.isEmpty() && (contentType || fileName.contains("."))) {
            final String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
            String[] allowedExt = { "jpg", "jpeg", "png", "gif", "bmp" };
            for (String s : allowedExt) {
                String allowed = (contentType ? "image/" : "") + s;
                if (extension.equals(allowed)) {
                    return true;
                }
            }
        }
        return false;
    }

    @PostMapping("/profile/picture")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        String redirectUrl = "redirect:/profile";

        UserAuth user = userService.getLoggedInUser();
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        if (file.getSize() > 2*1024*1024) {
            redirectAttributes.addFlashAttribute("avatar_errors", Collections.singletonList("File should be smaller than 2 MB"));
            return redirectUrl;
        }

        try {
            BufferedImage image = Utils.readImage(file);
            if (image == null || !verifyType(file.getOriginalFilename(), false) || !verifyType(file.getContentType(), true)) {
                redirectAttributes.addFlashAttribute("avatar_errors", Collections.singletonList("File type must be image"));
                return redirectUrl;
            }

            log.info("Uploading photo " + file.getOriginalFilename() + " for " + user.getUserId());
            log.info(String.format("Original Image Size : %s", Utils.humanReadableByteCount(file.getSize(), true)));
            InputStream resizedImage = ImageUtils.generateThumbnail(image, extension, 1000);
            if (resizedImage == null) // File is appropriate, hence no thumbnail generated
                resizedImage = file.getInputStream();
            String link = firebaseService.uploadFile("profile/" + user.getUserId() + "/profile." + extension, file.getContentType(), resizedImage );
            userDetailService.updateAvatar(user, link);
            redirectAttributes.addFlashAttribute("avatar_success", Collections.singletonList("Profile Picture Updated"));
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("avatar_errors", Collections.singletonList("Unknown Error"));
        }

        return redirectUrl;
    }

}
