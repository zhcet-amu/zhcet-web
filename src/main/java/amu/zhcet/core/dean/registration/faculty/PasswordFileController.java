package amu.zhcet.core.dean.registration.faculty;

import amu.zhcet.storage.file.FileSystemStorageService;
import amu.zhcet.storage.file.FileType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Controller
public class PasswordFileController {

    private final FileSystemStorageService systemStorageService;

    @Autowired
    public PasswordFileController(FileSystemStorageService systemStorageService) {
        this.systemStorageService = systemStorageService;
    }

    @GetMapping("/dean/password/{id}")
    public void downloadCsv(HttpServletResponse response, @PathVariable("id") PasswordFile passwordFile) throws IOException {
        if (passwordFile == null || passwordFile.isExpired()) return;

        response.setContentType("text/csv");

        response.setHeader("Content-disposition", "attachment;filename=passwords.csv");

        List<String> lines = Files.readAllLines(systemStorageService.load(FileType.CSV, passwordFile.getLink()));
        for (String line : lines) {
            response.getOutputStream().println(line);
        }

        response.getOutputStream().flush();
    }

}
