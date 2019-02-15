package amu.zhcet.core.admin.dean.registration.course.floated;

import com.j256.simplecsv.common.CsvColumn;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FloatedCourseUpload {
    @CsvColumn
    private String course;
}