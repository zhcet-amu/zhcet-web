package amu.zhcet.firebase.messaging;

import amu.zhcet.firebase.messaging.model.SendRequest;
import amu.zhcet.firebase.messaging.model.SendResponse;
import feign.HeaderMap;
import feign.Headers;
import feign.RequestLine;

import java.util.Map;

public interface MessagingClient {

    @RequestLine("POST")
    @Headers("Content-Type: application/json")
    SendResponse sendMessage(SendRequest request, @HeaderMap Map<String, Object>headers);

}
