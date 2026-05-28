package net.whynotjava.better_email.emAPI.login;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Base64.Decoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import net.whynotjava.better_email.*;

@RestController("/emapi/v1/signup")
public class Signup {

    @Autowired
    Database db;

    @PostMapping("")
    public HttpEntity signup(@RequestBody SignupJSON signup){
        System.out.println("signup from signup!!!!!!! :)");
        try (Connection conn = db.getDB().getConnection()){
            Decoder decoder = Base64.getDecoder();
            byte[] signingKey = decoder.decode(signup.getSigningKey());
            byte[] X25519Key = decoder.decode(signup.getX25519Key());
            byte[] nonce = decoder.decode(signup.getNonce());
            byte[] vault = decoder.decode(signup.getVault());

            if(vault.length > Constants.VAULT_LENGTH){
                return new HttpEntity<String>("{}", HttpStatus.BAD_REQUEST);
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
}
