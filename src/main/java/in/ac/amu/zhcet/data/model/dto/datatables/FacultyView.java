package in.ac.amu.zhcet.data.model.dto.datatables;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

@Data
@EqualsAndHashCode(callSuper = false)
public class FacultyView extends UserView {

    @JsonView(DataTablesOutput.View.class)
    private String facultyId;
    @JsonView(DataTablesOutput.View.class)
    private String designation;
    @JsonView(DataTablesOutput.View.class)
    private boolean working;
    @JsonView(DataTablesOutput.View.class)
    private String createdAt;
    @JsonView(DataTablesOutput.View.class)
    private String modifiedAt;
    @JsonView(DataTablesOutput.View.class)
    private String createdBy;
    @JsonView(DataTablesOutput.View.class)
    private String modifiedBy;
    @JsonView(DataTablesOutput.View.class)
    private String version;

}
