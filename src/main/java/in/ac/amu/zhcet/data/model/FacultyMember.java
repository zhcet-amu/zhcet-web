package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.model.base.user.UserAuth;
import in.ac.amu.zhcet.data.model.base.user.CustomPrincipal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
public class FacultyMember extends CustomPrincipal {
    @Id
    private String facultyId;

    public FacultyMember(UserAuth user) {
        super(user);
        this.facultyId = user.getUserId();
    }
}
