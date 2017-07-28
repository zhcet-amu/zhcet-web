package in.ac.amu.zhcet.data.service;

import in.ac.amu.zhcet.data.model.dto.AttendanceUpload;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.constraint.Unique;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.exception.SuperCsvReflectionException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AttendanceUploadService {

    private final RegisteredCourseService registeredCourseService;

    @Autowired
    public AttendanceUploadService(RegisteredCourseService registeredCourseService) {
        this.registeredCourseService = registeredCourseService;
    }

    @Data
    public static class UploadResult {
        @NonNull
        private List<String> errors = new ArrayList<>();
        @NonNull
        private List<AttendanceUpload> attendanceUploads = new ArrayList<>();
    }

    private static CellProcessor[] getProcessors() {

        final String enrolment = "[A-Z]{2}[0-9]{4}";
        StrRegEx.registerMessage(enrolment, "must be a valid enrolment number");

        return new CellProcessor[] {
                new NotNull(new StrRegEx(enrolment)),
                new NotNull(new ParseInt()),
                new NotNull(new ParseInt())
        };
    }

    public UploadResult handleUpload(MultipartFile file) throws IOException {
        UploadResult uploadResult = new UploadResult();

        if (!file.getContentType().equals("text/csv")) {
            logAndError(uploadResult, "Uploaded file is not of CSV format");

            return uploadResult;
        }

        ICsvBeanReader beanReader = null;
        try {

            beanReader = new CsvBeanReader(new InputStreamReader(file.getInputStream()), CsvPreference.STANDARD_PREFERENCE);
            final String[] header = beanReader.getHeader(true);

            AttendanceUpload attendance = null;
            do {
                try {
                    attendance = beanReader.read(AttendanceUpload.class, header, getProcessors());
                    if (attendance == null)
                        return uploadResult;

                    uploadResult.attendanceUploads.add(attendance);
                } catch (SuperCsvConstraintViolationException cve) {
                    cve.printStackTrace();
                    CsvContext csvContext = cve.getCsvContext();
                    logAndError(uploadResult, cve.getLocalizedMessage() + "\n" + "In line " + csvContext.getLineNumber() + ", row " + csvContext.getRowSource());
                }
            } while (attendance != null);

        } catch (SuperCsvReflectionException header) {
            header.printStackTrace();
            logAndError(uploadResult,
                    "Headers not mapped correctly.\n" +
                    "Please check that CSV headers are present and are in this format and order:\n" +
                    "<strong>student,attended,delivered</strong>\n");
        } finally {
            if( beanReader != null )
                beanReader.close();
        }

        return uploadResult;
    }

    private static void logAndError(UploadResult uploadResult, String error) {
        log.error(error);
        uploadResult.errors.add(error);
    }

}
