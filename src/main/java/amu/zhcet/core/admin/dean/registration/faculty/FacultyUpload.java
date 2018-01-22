package amu.zhcet.core.admin.dean.registration.faculty;

import amu.zhcet.data.user.Gender;
import amu.zhcet.storage.csv.CustomEnumConverter;
import com.j256.simplecsv.common.CsvColumn;
import lombok.Data;

@Data
public class FacultyUpload {
    @CsvColumn(columnName = "faculty_id", mustNotBeBlank = true)
    private String facultyId;

    @CsvColumn(mustNotBeBlank = true)
    private String name;

    @CsvColumn(mustBeSupplied = false, converterClass = CustomEnumConverter.class)
    private Gender gender;

    @CsvColumn(mustNotBeBlank = true)
    private String designation;

    @CsvColumn(mustNotBeBlank = true)
    private String department;

    @CsvColumn(mustBeSupplied = false)
    private String password;
}
