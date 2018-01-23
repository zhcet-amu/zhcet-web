package amu.zhcet.firebase.messaging.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String token;
    private String topic;
    private String name;
    private NotificationBody notification;
    private WebPushConfig webpush;
    private DataBody data;
}
