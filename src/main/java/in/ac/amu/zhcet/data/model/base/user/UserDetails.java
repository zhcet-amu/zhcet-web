package in.ac.amu.zhcet.data.model.base.user;

import in.ac.amu.zhcet.data.model.Department;
import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Embeddable
@Data
public class UserDetails {
    private String email;
    private String avatarUrl;
    private String addressLine1;
    private String addressLine2;
    private boolean active = true;

    @ManyToOne
    @NotNull
    private Department department;

    private String[] phoneNumbers;
}
