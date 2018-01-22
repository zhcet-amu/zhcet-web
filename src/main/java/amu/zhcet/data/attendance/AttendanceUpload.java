package amu.zhcet.data.attendance;

import amu.zhcet.common.model.Meta;
import com.j256.simplecsv.common.CsvColumn;
import lombok.Data;

@Data
public class AttendanceUpload implements Meta {
    private String meta;
    @CsvColumn(columnName = "enrolment_no", mustNotBeBlank = true)
    private String enrolmentNo;
    @CsvColumn(columnName = "faculty_no", mustBeSupplied = false)
    private String facultyNo;
    @CsvColumn(mustBeSupplied = false)
    private String name;
    @CsvColumn(mustBeSupplied = false)
    private String section;
    @CsvColumn(mustNotBeBlank = true)
    private int attended;
    @CsvColumn(mustNotBeBlank = true)
    private int delivered;
    @CsvColumn(mustBeSupplied = false)
    private float percentage;
    @CsvColumn(mustBeSupplied = false)
    private String remark;
}
