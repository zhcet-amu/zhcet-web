package in.ac.amu.zhcet.data.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import in.ac.amu.zhcet.data.model.base.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

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
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;

    private String phoneNumbers;

    public void setPhoneNumbers(String[] phoneNumbers) {
        this.phoneNumbers = String.join(",", phoneNumbers);
    }

    public String[] getPhoneNumbers() {
        if (phoneNumbers != null)
            return phoneNumbers.split(",");

        return null;
    }
}
