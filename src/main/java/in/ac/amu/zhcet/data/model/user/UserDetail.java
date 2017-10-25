package in.ac.amu.zhcet.data.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import in.ac.amu.zhcet.data.model.base.BaseEntity;
import in.ac.amu.zhcet.data.type.Gender;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Data
@Entity
@Audited
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "userAuth")
@ToString(exclude = "userAuth")
public class UserDetail extends BaseEntity {
    @Id
    private String userId;

    @OneToOne
    @PrimaryKeyJoinColumn
    @JsonIgnore
    UserAuth userAuth;

    private String avatarUrl;
    private String originalAvatarUrl;
    @org.hibernate.annotations.Type(type = "text")
    private String description;
    @Size(max = 500)
    private String address;
    @Size(max = 255)
    private String city;
    @Size(max = 255)
    private String state;
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private ZonedDateTime avatarUpdated;

    @Size(max = 255)
    private String phoneNumbers;

    public void setPhoneNumberList(String[] phoneNumbers) {
        this.phoneNumbers = String.join(",", phoneNumbers);
    }

    public String[] getPhoneNumberList() {
        if (phoneNumbers != null)
            return phoneNumbers.split(",");

        return null;
    }
}
