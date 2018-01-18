package amu.zhcet.core.admin.dean.registration.faculty;

import amu.zhcet.storage.file.FileSystemStorageService;
import amu.zhcet.storage.file.FileType;
import com.j256.simplecsv.processor.CsvProcessor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
class PasswordFileService {

    private final PasswordFileRepository passwordFileRepository;
    private final FileSystemStorageService storageService;
    private final TaskScheduler scheduler;

    @Autowired
    public PasswordFileService(PasswordFileRepository passwordFileRepository, FileSystemStorageService storageService, TaskScheduler scheduler) {
        this.passwordFileRepository = passwordFileRepository;
        this.storageService = storageService;
        this.scheduler = scheduler;
    }

    public PasswordFile create(List<FacultyUpload> facultyUploads) throws IOException {
        String filename = storageService.generateFileName("faculty_password.csv");

        Path filePath = storageService.load(FileType.CSV, filename);
        File newFile = filePath.toFile();

        CsvProcessor<FacultyUpload> csvProcessor = new CsvProcessor<>(FacultyUpload.class);
        csvProcessor.writeAll(newFile, facultyUploads, true);

        PasswordFile passwordFile = new PasswordFile(filename);
        passwordFileRepository.save(passwordFile);

        Date expiryDate = Date.from(passwordFile.getExpiryTime().toInstant(OffsetDateTime.now().getOffset()));
        log.info("Scheduling to delete password file at : {}", expiryDate);
        scheduler.schedule(new DeletePasswordFile(passwordFile.getId()), expiryDate);

        return passwordFile;
    }

    @Data
    private class DeletePasswordFile implements Runnable {

        private final String id;

        private DeletePasswordFile(String id) {
            this.id = id;
        }

        @Override
        public void run() {
            PasswordFile passwordFile = passwordFileRepository.findOne(id);
            log.warn("Deleting password file {}. Created Time: {}, Expiry Time: {}, Current Time: {}",
                    id, passwordFile.getCreatedTime(), passwordFile.getExpiryTime(), LocalDateTime.now());
            passwordFile.setDeleted(true);

            storageService.delete(FileType.CSV, passwordFile.getLink());
            passwordFileRepository.save(passwordFile);
        }
    }

}
