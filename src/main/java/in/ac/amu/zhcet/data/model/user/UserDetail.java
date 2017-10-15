package in.ac.amu.zhcet.data.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import in.ac.amu.zhcet.data.model.base.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import java.time.ZonedDateTime;

@Data
@Entity
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
    @org.hibernate.annotations.Type(type = "text")
    private String description;
    private String address;
    private String city;
    private String state;

    private ZonedDateTime avatarUpdated;

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
