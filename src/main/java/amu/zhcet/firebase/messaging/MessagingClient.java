package amu.zhcet.firebase.messaging;

import amu.zhcet.firebase.messaging.model.SendRequest;
import amu.zhcet.firebase.messaging.model.SendResponse;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

@Headers("Content-Type: application/json")
public interface MessagingClient {

    @RequestLine("POST")
    @Headers("Authorization: Bearer {token}")
    SendResponse sendMessage(@Param("token") String token, SendRequest request);

}
