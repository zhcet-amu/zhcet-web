package in.ac.amu.zhcet.service.firebase.messaging.data.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationBody {
    private String title;
    private String body;
    private final String icon = "https://zhcet-backend.firebaseapp.com/static/img/icon.png";
}
