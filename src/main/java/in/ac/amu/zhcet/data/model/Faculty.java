package in.ac.amu.zhcet.data.model;

import javax.persistence.*;

@Entity
public class Faculty {
    @Id
    private final Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private BaseUser user;

    protected Faculty(){id = null;}

    public Faculty(BaseUser user) {
        this.id = user.getId();
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public BaseUser getUser() {
        return user;
    }

    public void setUser(BaseUser user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "ID: {"+this.id;
    }
}
