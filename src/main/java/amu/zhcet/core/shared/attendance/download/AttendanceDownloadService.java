package amu.zhcet.core.shared.attendance.download;

import amu.zhcet.common.utils.SortUtils;
import amu.zhcet.data.attendance.Attendance;
import amu.zhcet.data.attendance.AttendanceUpload;
import amu.zhcet.data.config.ConfigurationService;
import amu.zhcet.data.course.registration.CourseRegistration;
import amu.zhcet.storage.file.FileSystemStorageService;
import amu.zhcet.storage.file.FileType;
import com.j256.simplecsv.processor.CsvProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
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
    private final ConfigurationService configurationService;

    @Autowired
    public AttendanceDownloadService(FileSystemStorageService fileSystemStorageService, CsvProcessor<AttendanceUpload> csvProcessor, ConfigurationService configurationService) {
        this.fileSystemStorageService = fileSystemStorageService;
        this.csvProcessor = csvProcessor;
        this.configurationService = configurationService;
    }

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    private static float calculateSafePercentage(Attendance attendance) {
        if (attendance.getDelivered() == 0)
            return 0;

        return round(attendance.getAttended()*100/(float)attendance.getDelivered(), 2);
    }

    private AttendanceUpload fromCourseRegistration(CourseRegistration courseRegistration) {
        AttendanceUpload attendanceUpload = new AttendanceUpload();
        attendanceUpload.setEnrolment_no(courseRegistration.getStudent().getEnrolmentNumber());
        attendanceUpload.setFaculty_no(courseRegistration.getStudent().getFacultyNumber());
        attendanceUpload.setName(courseRegistration.getStudent().getUser().getName());
        attendanceUpload.setSection(courseRegistration.getStudent().getSection());
        attendanceUpload.setAttended(courseRegistration.getAttendance().getAttended());
        attendanceUpload.setDelivered(courseRegistration.getAttendance().getDelivered());
        float percentage = calculateSafePercentage(courseRegistration.getAttendance());
        attendanceUpload.setPercentage(percentage);
        if (percentage < configurationService.getThreshold() && courseRegistration.getAttendance().getDelivered() != 0)
            attendanceUpload.setRemark("Short");

        return attendanceUpload;
    }

    private List<String> attendanceCsv(String authority, String meta, List<CourseRegistration> courseRegistrations) throws IOException {
        SortUtils.sortCourseAttendance(courseRegistrations);
        String fileName = fileSystemStorageService.generateFileName(authority + "_" + meta + ".csv");

        log.info("Writing CSV to a file : {}", fileName);

        csvProcessor.writeAll(fileSystemStorageService.load(FileType.CSV, fileName).toFile(),
                courseRegistrations.stream()
                    .map(this::fromCourseRegistration)
                    .collect(Collectors.toList()), true
        );

        log.info("File Written!");

        return Files.readAllLines(fileSystemStorageService.load(FileType.CSV, fileName));
    }

    public void download(String suffix, String authority, List<CourseRegistration> courseRegistrations, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-disposition", "attachment;filename=attendance_" + suffix + ".csv");

        List<String> lines = attendanceCsv(authority, suffix, courseRegistrations);
        for (String line : lines) {
            response.getOutputStream().println(line);
        }

        response.getOutputStream().flush();
    }

}
