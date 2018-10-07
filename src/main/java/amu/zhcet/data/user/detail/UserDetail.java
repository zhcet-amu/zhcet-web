package amu.zhcet.data.user.detail;

import amu.zhcet.common.model.BaseEntity;
import amu.zhcet.data.user.Gender;
import amu.zhcet.data.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.ZonedDateTime;

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

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    @JsonIgnore
    User user;

    private String avatarUrl;
    private String originalAvatarUrl;
    @Type(type = "text")
    private String description;
    @Size(max = 500)
    private String address;
    @Size(max = 255)
    private String city;
    @Size(max = 255)
    private String state;
    @Past
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    private ZonedDateTime avatarUpdated;

    @Size(max = 255)
    private String phoneNumbers;
    private String firebaseClaims;

    public void setPhoneNumberList(String[] phoneNumbers) {
        this.phoneNumbers = String.join(",", phoneNumbers);
    }

    public String[] getPhoneNumberList() {
        if (phoneNumbers != null)
            return phoneNumbers.split(",");

        return null;
    }
}
