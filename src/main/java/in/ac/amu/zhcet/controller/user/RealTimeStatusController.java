package in.ac.amu.zhcet.controller.user;

import in.ac.amu.zhcet.service.realtime.RealTimeStatus;
import in.ac.amu.zhcet.service.realtime.RealTimeStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class RealTimeStatusController {

    private final RealTimeStatusService realTimeStatusService;

    public RealTimeStatusController(RealTimeStatusService realTimeStatusService) {
        this.realTimeStatusService = realTimeStatusService;
    }

    @GetMapping("/task/status/{id}")
    public RealTimeStatus realTimeStatus(@PathVariable String id) {
        return realTimeStatusService.get(id);
    }

}
