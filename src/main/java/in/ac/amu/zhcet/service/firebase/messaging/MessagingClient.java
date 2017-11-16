package in.ac.amu.zhcet.service.firebase.messaging;

import feign.HeaderMap;
import feign.Headers;
import feign.RequestLine;
import in.ac.amu.zhcet.service.firebase.messaging.data.SendRequest;
import in.ac.amu.zhcet.service.firebase.messaging.data.SendResponse;

import java.util.Map;

public interface MessagingClient {

    @RequestLine("POST")
    @Headers("Content-Type: application/json")
    SendResponse sendMessage(SendRequest request, @HeaderMap Map<String, Object>headers);

}
