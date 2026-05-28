package net.whynotjava.better_email;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class StaticRouting {

    @GetMapping("/DATABASE_URL")
    public String index(){
        return Constants.DB_URL;
    }
    @GetMapping("/login")
    public String login(){
        return "redirect:/login/";
    }
    @GetMapping("/login/")
    public String logingIndex(){
        return "forward:/login/index.html";
    }
}