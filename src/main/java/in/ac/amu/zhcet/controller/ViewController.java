package in.ac.amu.zhcet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {

    @RequestMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @RequestMapping("/")
    public String homePage() {
        return "home";
    }

}
