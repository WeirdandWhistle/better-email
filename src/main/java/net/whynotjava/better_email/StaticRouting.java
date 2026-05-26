package net.whynotjava.better_email;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StaticRouting {

    @GetMapping("/DATABASE_URL")
    public String index(){
        return Constants.DB_URL;
    }
}