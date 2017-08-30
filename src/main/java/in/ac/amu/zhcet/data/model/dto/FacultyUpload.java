package in.ac.amu.zhcet.data.model.dto;

import com.j256.simplecsv.common.CsvColumn;
import lombok.Data;

@Data
public class FacultyUpload {
    @CsvColumn(columnName = "faculty_id", mustNotBeBlank = true)
    private String facultyId;

    @CsvColumn(columnName = "name", mustNotBeBlank = true)
    private String name;

    @CsvColumn(columnName = "department", mustNotBeBlank = true)
    private String department;

    @CsvColumn(columnName = "password", mustBeSupplied = false)
    private String password;
}
