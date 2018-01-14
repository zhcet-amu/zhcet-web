package amu.zhcet.core.dean.registration.student;

import amu.zhcet.data.user.Gender;
import amu.zhcet.data.user.student.HallCode;
import com.j256.simplecsv.common.CsvColumn;
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
