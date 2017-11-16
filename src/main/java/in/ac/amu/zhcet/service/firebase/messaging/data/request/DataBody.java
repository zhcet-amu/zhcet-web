package in.ac.amu.zhcet.service.firebase.messaging.data.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataBody {
    private String title;
    private String message;
    private String sender;
    private String sentTime;
}
