package amu.zhcet.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class ViewController {

    @GetMapping("/terms")
    public String getTerms() {
        return "terms_of_service";
    }

    @GetMapping("/privacy")
    public String getPrivacyPolicy() {
        return "privacy_policy";
    }

}
