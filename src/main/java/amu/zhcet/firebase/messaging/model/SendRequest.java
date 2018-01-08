package amu.zhcet.firebase.messaging.model;

import amu.zhcet.firebase.messaging.model.request.DataBody;
import amu.zhcet.firebase.messaging.model.request.NotificationBody;
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
