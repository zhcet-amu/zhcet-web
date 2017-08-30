package in.ac.amu.zhcet.data.model.dto;

import com.j256.simplecsv.common.CsvColumn;
import lombok.Data;

@Data
public class RegistrationUpload {
    @CsvColumn(columnName = "enrolment_no", mustNotBeBlank = true)
    private String enrolmentNo;
}
