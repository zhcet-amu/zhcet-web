package amu.zhcet.core.admin.attendance;

import amu.zhcet.common.utils.SortUtils;
import amu.zhcet.core.admin.faculty.attendance.upload.AttendanceUpload;
import amu.zhcet.data.course.registration.CourseRegistration;
import amu.zhcet.storage.FileSystemStorageService;
import amu.zhcet.storage.FileType;
import com.j256.simplecsv.processor.CsvProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Shared service between dean, department and faculty members for downloading attendance
 * For specific features, please look into `dean`, `department` and `faculty` packages
 */
@Slf4j
@Service
public class AttendanceDownloadService {

    private final FileSystemStorageService fileSystemStorageService;
    private final CsvProcessor<AttendanceUpload> csvProcessor;
    private final AttendanceMapper attendanceMapper;

    @Autowired
    public AttendanceDownloadService(FileSystemStorageService fileSystemStorageService,
                                     CsvProcessor<AttendanceUpload> csvProcessor,
                                     AttendanceMapper attendanceMapper) {
        this.fileSystemStorageService = fileSystemStorageService;
        this.csvProcessor = csvProcessor;
        this.attendanceMapper = attendanceMapper;
    }

    public InputStream getAttendanceStream(String authority, String meta, List<CourseRegistration> courseRegistrations) throws IOException {
        SortUtils.sortCourseAttendance(courseRegistrations);
        String fileName = fileSystemStorageService.generateFileName(authority + "_" + meta + ".csv");

        log.debug("Writing CSV to a file : {}", fileName);

        csvProcessor.writeAll(fileSystemStorageService.load(FileType.CSV, fileName).toFile(),
                courseRegistrations.stream()
                    .map(attendanceMapper::fromCourseRegistration)
                    .collect(Collectors.toList()), true
        );

        log.debug("File Written!");

        return new FileInputStream(fileSystemStorageService.load(FileType.CSV, fileName).toFile());
    }

}
