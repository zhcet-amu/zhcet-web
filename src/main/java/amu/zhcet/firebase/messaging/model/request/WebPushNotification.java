package amu.zhcet.firebase.messaging.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebPushNotification {
    private String title;
    private String body;
    private final String icon = "https://zhcet-backend.firebaseapp.com/static/img/icon.png";
}
