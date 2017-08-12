package in.ac.amu.zhcet.data.model.dto;

import com.j256.simplecsv.common.CsvColumn;
import lombok.Data;

@Data
public class StudentUpload {
    @CsvColumn(columnName = "enrolment_no", mustNotBeBlank = true)
    private String enrolmentNo;
    @CsvColumn(columnName = "faculty_no", mustNotBeBlank = true)
    private String facultyNo;
    @CsvColumn(mustNotBeBlank = true)
    private String name;
    @CsvColumn(mustNotBeBlank = true)
    private String department;
}
