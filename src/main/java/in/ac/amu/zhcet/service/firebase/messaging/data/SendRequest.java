package in.ac.amu.zhcet.service.firebase.messaging.data;

import in.ac.amu.zhcet.service.firebase.messaging.data.request.DataBody;
import in.ac.amu.zhcet.service.firebase.messaging.data.request.NotificationBody;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendRequest {
    private String to;
    private NotificationBody notification;
    private DataBody data;
}
