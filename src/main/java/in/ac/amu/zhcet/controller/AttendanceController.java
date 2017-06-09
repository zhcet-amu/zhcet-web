package in.ac.amu.zhcet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AttendanceController {

    @RequestMapping("/attendance")
    public String attendance() {
        return "attendance";
    }

}
