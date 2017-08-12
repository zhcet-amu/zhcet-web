package in.ac.amu.zhcet.data.model.dto;

import com.j256.simplecsv.common.CsvColumn;
import lombok.Data;

@Data
public class AttendanceUpload {
    @CsvColumn(mustNotBeBlank = true)
    private String student;
    @CsvColumn(mustNotBeBlank = true)
    private int attended;
    @CsvColumn(mustNotBeBlank = true)
    private int delivered;
}
