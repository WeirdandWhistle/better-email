package net.whynotjava.better_email.emAPI.user;

import static net.whynotjava.better_email.Util.badRequest;
import static net.whynotjava.better_email.Util.error;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import net.whynotjava.better_email.Database;
import net.whynotjava.better_email.emAPI.login.Signup;
import net.whynotjava.better_email.emAPI.verify.Verify;

@RestController
public class User {

    @Autowired
    Signup signup;

    @Autowired
    Database db;

    Logger log = LoggerFactory.getLogger(getClass());

    @GetMapping("/emapi/v1/user")
    public ResponseEntity<String> userGet(@RequestParam(required = false) String username,
                                            @RequestParam(required = false) String UUID,
                                            @RequestParam(required = false) String signingKey,
                                            @RequestParam(required = false) String X25519Key){
        return signup.signupGet(username, UUID, signingKey, X25519Key);
    }

    @PutMapping("/emapi/v1/user")
    public ResponseEntity<String> userPut(@RequestBody UserPutJSON json){
        try (Connection conn = db.getDB().getConnection()){

            boolean verifyed = Verify.verifyUser(conn, json.getSignatureBytes(), json.getChallengeBytes());
            if(!verifyed){
                log.info("attempted impersonation!");
                return badRequest("User could not be verifyed.");
            }
            log.info("Succful verifacation!");

            switch (json.getType()) {
                case "displayName":
                    // PreparedStatement ps = conn.prepareStatement("UPDATE users SET displayName=? WHERE")
                    break;
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            return error(500, "userPut Exception: "+e.getMessage());
    }
        return badRequest("No such type.");
    }
}
