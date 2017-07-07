package in.ac.amu.zhcet.data.model;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
@Data
public class UserDetails {
    private String email;
    private String avatarUrl;
    private String addressLine1;
    private String addressLine2;
    private boolean isActive = true;

    @ManyToOne
    private Department department;

    private String[] phoneNumbers;
}
