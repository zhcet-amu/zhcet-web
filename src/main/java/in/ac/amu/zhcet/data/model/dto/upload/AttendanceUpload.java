package in.ac.amu.zhcet.data.model.dto.upload;

import com.j256.simplecsv.common.CsvColumn;
import lombok.Data;

@Data
public class AttendanceUpload {
    @CsvColumn(mustNotBeBlank = true)
    private String enrolment_no;
    @CsvColumn(mustBeSupplied = false)
    private String faculty_no;
    @CsvColumn(mustBeSupplied = false)
    private String name;
    @CsvColumn(mustBeSupplied = false)
    private String section;
    @CsvColumn(mustNotBeBlank = true)
    private int attended;
    @CsvColumn(mustNotBeBlank = true)
    private int delivered;
}
