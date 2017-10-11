package in.ac.amu.zhcet.service;


import com.j256.simplecsv.processor.CsvProcessor;
import in.ac.amu.zhcet.data.model.CourseRegistration;
import in.ac.amu.zhcet.data.model.dto.upload.AttendanceUpload;
import in.ac.amu.zhcet.service.file.FileSystemStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AttendanceDownloadService {

    private final FileSystemStorageService fileSystemStorageService;
    private final CsvProcessor<AttendanceUpload> csvProcessor;

    @Autowired
    public AttendanceDownloadService(FileSystemStorageService fileSystemStorageService, CsvProcessor<AttendanceUpload> csvProcessor) {
        this.fileSystemStorageService = fileSystemStorageService;
        this.csvProcessor = csvProcessor;
    }

    private static AttendanceUpload fromCourseRegistration(CourseRegistration courseRegistration) {
        AttendanceUpload attendanceUpload = new AttendanceUpload();
        attendanceUpload.setEnrolment_no(courseRegistration.getStudent().getEnrolmentNumber());
        attendanceUpload.setFaculty_no(courseRegistration.getStudent().getFacultyNumber());
        attendanceUpload.setName(courseRegistration.getStudent().getUser().getName());
        attendanceUpload.setSection(courseRegistration.getStudent().getSection());
        attendanceUpload.setAttended(courseRegistration.getAttendance().getAttended());
        attendanceUpload.setDelivered(courseRegistration.getAttendance().getDelivered());

        return attendanceUpload;
    }

    public List<String> attendanceCsv(String authority, String meta, List<CourseRegistration> courseRegistrations) throws IOException {
        String fileName = fileSystemStorageService.generateFileName(authority + "_" + meta + ".csv");

        log.info("Writing CSV to a file : {}", fileName);

        csvProcessor.writeAll(fileSystemStorageService.load(fileName).toFile(),
                courseRegistrations.stream()
                    .map(AttendanceDownloadService::fromCourseRegistration)
                    .collect(Collectors.toList()), true
        );

        log.info("File Written!");

        return Files.readAllLines(fileSystemStorageService.load(fileName));
    }

}
