package amu.zhcet.data.user.detail;

import amu.zhcet.common.model.BaseEntity;
import amu.zhcet.data.user.Gender;
import amu.zhcet.data.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.Date;

@Data
@Entity
@Audited
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "user")
@ToString(exclude = "user")
public class UserDetail extends BaseEntity {
    @Id
    private String userId;

    @OneToOne
    @PrimaryKeyJoinColumn
    @JsonIgnore
    User user;

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
    @Past
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dob;

    private ZonedDateTime avatarUpdated;

    @Size(max = 255)
    private String phoneNumbers;
    private String firebaseClaims;
    private String fcmToken;

    public void setPhoneNumberList(String[] phoneNumbers) {
        this.phoneNumbers = String.join(",", phoneNumbers);
    }

    public String[] getPhoneNumberList() {
        if (phoneNumbers != null)
            return phoneNumbers.split(",");

        return null;
    }
}
