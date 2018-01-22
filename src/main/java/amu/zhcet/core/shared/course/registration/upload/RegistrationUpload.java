package amu.zhcet.core.shared.course.registration.upload;

import com.j256.simplecsv.common.CsvColumn;
import lombok.Data;

@Data
public class RegistrationUpload {
    @CsvColumn(columnName = "facultyNo", mustNotBeBlank = true)
    private String facultyNo;
    @CsvColumn
    private char mode;
}
