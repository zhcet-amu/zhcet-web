package amu.zhcet.firebase.messaging.model.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebPushConfig {
    private DataBody data;
    private WebPushNotification notification;
}
