package in.ac.amu.zhcet.controller;

import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.Student;
import in.ac.amu.zhcet.data.model.base.user.Type;
import in.ac.amu.zhcet.data.model.base.user.UserAuth;
import in.ac.amu.zhcet.data.model.base.user.UserDetail;
import in.ac.amu.zhcet.data.model.token.VerificationToken;
import in.ac.amu.zhcet.data.service.FacultyService;
import in.ac.amu.zhcet.data.service.FirebaseService;
import in.ac.amu.zhcet.data.service.StudentService;
import in.ac.amu.zhcet.data.service.UserService;
import in.ac.amu.zhcet.data.service.token.DuplicateEmailException;
import in.ac.amu.zhcet.data.service.token.EmailVerificationService;
import in.ac.amu.zhcet.data.service.user.UserDetailService;
import in.ac.amu.zhcet.utils.ImageUtils;
import in.ac.amu.zhcet.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

@Slf4j
@Controller
public class ProfileController {

    private final UserService userService;
    private final StudentService studentService;
    private final FacultyService facultyService;
    private final UserDetailService userDetailService;
    private final FirebaseService firebaseService;
    private final EmailVerificationService emailVerificationService;

    @Autowired
    public ProfileController(UserService userService, StudentService studentService, FacultyService facultyService, UserDetailService userDetailService, FirebaseService firebaseService, EmailVerificationService emailVerificationService) {
        this.userService = userService;
        this.studentService = studentService;
        this.facultyService = facultyService;
        this.userDetailService = userDetailService;
        this.firebaseService = firebaseService;
        this.emailVerificationService = emailVerificationService;
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        UserAuth userAuth = userService.getLoggedInUser();
        model.addAttribute("user", userAuth);
        model.addAttribute("user_details", userAuth.getDetails());

        model.addAttribute("title", "Profile");
        model.addAttribute("subtitle", "Profile Settings for " + userAuth.getName());
        model.addAttribute("description", "Manage Profile Details and Information");

        if (userAuth.getType().equals(Type.STUDENT)) {
            Student student = studentService.getLoggedInStudent();
            model.addAttribute("student", student);
        } else {
            FacultyMember facultyMember = facultyService.getLoggedInMember();
            model.addAttribute("faculty", facultyMember);
        }

        return "profile";
    }

    private void sendVerificationLink(String email, String appUrl, RedirectAttributes redirectAttributes) {
        try {
            VerificationToken token = emailVerificationService.generate(email);
            emailVerificationService.sendMail(appUrl, token);
            redirectAttributes.addFlashAttribute("link_sent", "Verification link sent to '" + email + "'!");
        } catch (DuplicateEmailException de) {
            redirectAttributes.addFlashAttribute("duplicate_email", de.getMessage());
        }
    }

    @PostMapping("/profile/register_email")
    public String registerEmail(RedirectAttributes redirectAttributes, @RequestParam String email, HttpServletRequest request) {
        if (Utils.isValidEmail(email)) {
            sendVerificationLink(email, Utils.getAppUrl(request), redirectAttributes);
        } else {
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("invalid_email", "The provided email is invalid!");
        }

        return "redirect:/profile";
    }

    @PostMapping("/profile/confirm_email")
    public String registerEmail(RedirectAttributes redirectAttributes, HttpServletRequest request) {
        UserAuth user = userService.getLoggedInUser();
        String email = user.getEmail();

        if (Utils.isValidEmail(user.getEmail())) {
            sendVerificationLink(email, Utils.getAppUrl(request), redirectAttributes);
        } else {
            redirectAttributes.addFlashAttribute("invalid_email", "The provided email is invalid!");
        }

        return "redirect:/profile";
    }

    @GetMapping("/login/verify")
    public String resetPassword(Model model, @RequestParam("token") String token){
        String result = emailVerificationService.validate(token);
        if (result != null) {
            model.addAttribute("error", result);
        } else {
            emailVerificationService.confirmEmail(token);
            model.addAttribute("success", "Your email was successfully verified!");
        }
        return "verify_email";
    }

    @PostMapping("/profile/details")
    public String saveProfile(@ModelAttribute UserDetail userDetail, final RedirectAttributes redirectAttributes) {
        try {
            userDetailService.updateDetails(userService.getLoggedInUser(), userDetail);
            redirectAttributes.addFlashAttribute("success", true);
        } catch (Exception exc) {
            exc.printStackTrace();
            redirectAttributes.addFlashAttribute("errors", Collections.singletonList(exc.getMessage()));
        }

        return "redirect:/profile";
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
