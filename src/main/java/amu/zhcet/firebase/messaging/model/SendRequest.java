package amu.zhcet.firebase.messaging.model;

import amu.zhcet.firebase.messaging.model.request.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendRequest {
    private Message message;
}
