package in.ac.amu.zhcet.controller;

import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
public class FileUploadController {

    @PostMapping("/upload/attendance/{course_id}")
    public String uploadFile(Model model, @PathVariable String course_id, @RequestParam("file") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                String completeData = new String(bytes);
                System.out.print(completeData);
            } catch (Exception e) {

            }
        }

        return "redirect:/faculty/courses/{course_id}";

    }
}
