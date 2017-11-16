package in.ac.amu.zhcet.service.firebase.messaging.data;

import in.ac.amu.zhcet.service.firebase.messaging.data.response.Result;
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
