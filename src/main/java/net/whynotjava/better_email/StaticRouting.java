package net.whynotjava.better_email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class StaticRouting {

    Logger logger = LoggerFactory.getLogger(getClass());

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