package in.ac.amu.zhcet.data.model.dto.upload;

import com.j256.simplecsv.common.CsvColumn;
import in.ac.amu.zhcet.data.type.Gender;
import in.ac.amu.zhcet.data.type.HallCode;
import lombok.Data;

@Data
public class StudentUpload {
    @CsvColumn(columnName = "enrolment_no", mustNotBeBlank = true)
    private String enrolmentNumber;
    @CsvColumn(columnName = "faculty_no", mustNotBeBlank = true)
    private String facultyNumber;
    @CsvColumn(mustBeSupplied = false)
    private String section;
    @CsvColumn(mustNotBeBlank = true)
    private String name;
    @CsvColumn(mustBeSupplied = false)
    private Gender gender;
    @CsvColumn(mustBeSupplied = false)
    private HallCode hall;
    @CsvColumn(mustBeSupplied = false)
    private char status;
    @CsvColumn(columnName = "registration_year", mustBeSupplied = false)
    private int registrationYear;
    @CsvColumn(mustNotBeBlank = true)
    private String department;
}
