package net.whynotjava.better_email;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.whynotjava.better_email.Constants;

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