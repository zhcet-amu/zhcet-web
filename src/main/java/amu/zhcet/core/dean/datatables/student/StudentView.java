package amu.zhcet.core.dean.datatables.student;

import amu.zhcet.core.dean.datatables.UserView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

@Data
@EqualsAndHashCode(callSuper = false)
class StudentView extends UserView {

    @JsonView(DataTablesOutput.View.class)
    private String facultyNumber;
    @JsonView(DataTablesOutput.View.class)
    private String enrolmentNumber;
    @JsonView(DataTablesOutput.View.class)
    private String hallCode;
    @JsonView(DataTablesOutput.View.class)
    private String section;
    @JsonView(DataTablesOutput.View.class)
    private int registrationYear;
    @JsonView(DataTablesOutput.View.class)
    private char status;
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
