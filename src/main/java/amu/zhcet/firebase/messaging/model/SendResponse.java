package amu.zhcet.firebase.messaging.model;

import amu.zhcet.firebase.messaging.model.response.Result;
import lombok.Data;

import java.util.List;

@Data
public class SendResponse {
    private String multicast_id;
    private String success;
    private int failure;
    private int canonical_ids;
    private List<Result> results;
}
