package in.ac.amu.zhcet.data.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Faculty {
    @Id
    private final Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private BaseUser user;

    protected Faculty() {
        id = null;
    }

    public Faculty(BaseUser user) {
        this.id = user.getId();
        this.user = user;
    }
}
