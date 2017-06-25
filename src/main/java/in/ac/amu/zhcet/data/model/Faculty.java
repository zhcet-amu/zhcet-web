package in.ac.amu.zhcet.data.model;

import javax.persistence.*;

@Entity
public class Faculty {
    @Id
    private final Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private User user;

    protected Faculty(){id = null;}

    public Faculty(User user) {
        this.id = user.getId();
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "ID: {"+this.id;
    }
}
