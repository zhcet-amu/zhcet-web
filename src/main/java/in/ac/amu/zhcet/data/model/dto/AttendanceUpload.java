package in.ac.amu.zhcet.data.model.dto;

import lombok.Data;

@Data
public class AttendanceUpload {
    private String student;
    private int attended;
    private int delivered;
}
