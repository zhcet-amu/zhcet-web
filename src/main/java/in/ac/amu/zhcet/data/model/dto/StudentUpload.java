package in.ac.amu.zhcet.data.model.dto;

import com.j256.simplecsv.common.CsvColumn;
import lombok.Data;

@Data
public class StudentUpload {
    @CsvColumn(columnName = "enrolment_no", mustNotBeBlank = true)
    private String enrolmentNo;
    @CsvColumn(columnName = "faculty_no", mustNotBeBlank = true)
    private String facultyNo;
    @CsvColumn(mustBeSupplied = false)
    private String section;
    @CsvColumn(mustNotBeBlank = true)
    private String name;
    @CsvColumn(mustBeSupplied = false)
    private String hall;
    @CsvColumn(mustBeSupplied = false)
    private char status;
    @CsvColumn(columnName = "registration_year", mustBeSupplied = false)
    private int registrationYear;
    @CsvColumn(mustNotBeBlank = true)
    private String department;
}
