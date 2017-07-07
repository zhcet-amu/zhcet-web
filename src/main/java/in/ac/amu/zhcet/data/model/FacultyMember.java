package in.ac.amu.zhcet.data.model;

import in.ac.amu.zhcet.data.model.base.BaseEntity;
import in.ac.amu.zhcet.data.model.base.BaseUser;
import in.ac.amu.zhcet.data.model.base.UserDetails;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
public class FacultyMember extends BaseEntity {
    @Id
    private String facultyId;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private BaseUser user;

    @Embedded
    private UserDetails userDetails;

    public FacultyMember(BaseUser user) {
        this.facultyId = user.getUserId();
        this.user = user;
    }

    public FacultyMember(BaseUser user, String avatarUrl, String addressLine1, String addressLine2, String[] phoneNumbers) {
        this(user);
        userDetails.setAvatarUrl(avatarUrl);
        userDetails.setAddressLine1(addressLine1);
        userDetails.setAddressLine2(addressLine2);
        userDetails.setPhoneNumbers(phoneNumbers);
    }
}
