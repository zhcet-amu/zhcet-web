package in.ac.amu.zhcet.data.model.base.user;

import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.base.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "userAuth")
public class UserDetail extends BaseEntity {
    @Id
    private String userId;

    @OneToOne
    @PrimaryKeyJoinColumn
    UserAuth userAuth;

    private String avatarUrl;
    private String description;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    private Department department;

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
