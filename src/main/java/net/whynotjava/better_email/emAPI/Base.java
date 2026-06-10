package net.whynotjava.better_email.emAPI;

import static net.whynotjava.better_email.Util.OKRes;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Base {
    @GetMapping("/emapi")
    public ResponseEntity<String> base(){
        return OKRes;
    }
}
