package in.ac.amu.zhcet.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class PersistentLogin {
    @Id
    private String series;
    private String username;
    private String token;
    private Date lastUsed;
}
