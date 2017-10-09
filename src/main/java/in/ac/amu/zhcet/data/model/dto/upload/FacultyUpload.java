package in.ac.amu.zhcet.data.model.dto.upload;

import com.j256.simplecsv.common.CsvColumn;
import lombok.Data;

@Data
public class FacultyUpload {
    @CsvColumn(columnName = "faculty_id", mustNotBeBlank = true)
    private String facultyId;

    @CsvColumn(mustNotBeBlank = true)
    private String name;

    @CsvColumn(mustNotBeBlank = true)
    private String designation;

    @CsvColumn(mustNotBeBlank = true)
    private String department;

    @CsvColumn(mustBeSupplied = false)
    private String password;
}
