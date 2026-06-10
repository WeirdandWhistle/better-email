package net.whynotjava.better_email.emAPI.login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import static net.whynotjava.better_email.Constants.*;
import net.whynotjava.better_email.Database;
import net.whynotjava.better_email.Constants.CC;
import net.whynotjava.better_email.emAPI.Username;
import tools.jackson.databind.*;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

import static net.whynotjava.better_email.Util.CacheControl;
import static net.whynotjava.better_email.Util.OKRes;
import static net.whynotjava.better_email.Util.badRequest;
import static net.whynotjava.better_email.Util.convertBytesToUUID;
import static net.whynotjava.better_email.Util.convertUUIDToBytes;
import static net.whynotjava.better_email.Util.error;

@Controller
public class Signup {

    @Autowired
    Database db;

    Logger log = LoggerFactory.getLogger(getClass());

    @GetMapping("emapi/v1/signup")
    public ResponseEntity<String> signupGet(@RequestParam(required = false) String username,
                                            @RequestParam(required = false) String UUID,
                                            @RequestParam(required = false) String signingKey,
                                            @RequestParam(required = false) String X25519Key){
        try (Connection conn = db.getDB().getConnection()){
            ResultSet rs = null;
            if(username != null){
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username=? LIMIT 1;");
                ps.setString(1, username);
                rs = ps.executeQuery();
            } else if(UUID != null){
                byte[] uuidBytes = convertUUIDToBytes(java.util.UUID.fromString(UUID));
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE UUID=? LIMIT 1;");
                ps.setBytes(1, uuidBytes);
                rs = ps.executeQuery();
            } else if(signingKey != null){
                Base64.Decoder decoder = Base64.getUrlDecoder();
                byte[] singingKeyBytes = decoder.decode(signingKey);
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE signingKey=? LIMIT 1;");
                ps.setBytes(1, singingKeyBytes);
                rs = ps.executeQuery();
            }
            else if(X25519Key != null){
                Base64.Decoder decoder = Base64.getUrlDecoder();
                byte[] X25519KeyBytes = decoder.decode(X25519Key);
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE X25519Key=? LIMIT 1;");
                ps.setBytes(1, X25519KeyBytes);
                rs = ps.executeQuery();
            }

            if(rs == null){
                return badRequest("Must choose a search method.");
            }
            if(!rs.next()){
                return badRequest("User does not exist.");
            }

            JsonMapper mapper = JsonMapper.builder().build();
            Base64.Encoder encoder = Base64.getUrlEncoder();
            ObjectNode root = mapper.createObjectNode();
            root.put("UUID", convertBytesToUUID(rs.getBytes("UUID")).toString());
            root.put("X25519Key", encoder.encodeToString(rs.getBytes("X25519Key")));
            root.put("signingKey", encoder.encodeToString(rs.getBytes("signingKey")));
            root.put("nonce", encoder.encodeToString(rs.getBytes("nonce")));
            root.put("vault", encoder.encodeToString(rs.getBytes("vault")));
            root.put("username", rs.getString("username"));
            root.put("status",200);
            return new ResponseEntity<>(root.toString(), CacheControl("max-age="+(CC.USER_HOURS * 60 * 60)), HttpStatus.OK);
        
        } catch (Exception e) {
            log.error(e.getMessage());
            return error(500, "signupGet Exception"+e.getMessage());
        }    
    }
    
    @PostMapping("/emapi/v1/signup")
    public ResponseEntity<?> signupPost(@RequestBody SignupJSON signup){

        signup.log(log);

        try (Connection conn = db.getDB().getConnection()){
            Base64.Decoder decoder = Base64.getUrlDecoder();
            log.info("singingKey: " + signup.getSigningKey());
            byte[] signingKey = decoder.decode(signup.getSigningKey());
            byte[] X25519Key = decoder.decode(signup.getX25519Key());
            byte[] nonce = decoder.decode(signup.getNonce());
            byte[] vault = decoder.decode(signup.getVault());
            String username = signup.getUsername().toLowerCase();

            if(vault.length > VAULT_LENGTH){
                return badRequest("Vault is too big.");
            }
            if (signingKey.length != SINGING_KEY_LENGTH) {
                return badRequest("Signing key is the wrong length.");
            }
            if(X25519Key.length != KEY_LENGTH){
                return badRequest("X25519Key is the wrong length.");
            }
            if(nonce.length != NONCE_LENGTH){
                return badRequest("Nonce is the wrong length.");
            }
            if(username.length() > MAX_USERNAME_LENGTH){
                return badRequest("Username is too long.");
            }
            if(username.length() <= 0){
                return badRequest("Username does not exist.");
            }
            if(!Username.validateUsername(username)){
                return badRequest("Username is not valid.");
            }
            
            PreparedStatement ps = conn.prepareStatement("SELECT UUID FROM users WHERE signingKey=? OR X25519Key=? OR username=? LIMIT 1;");
            ps.setBytes(1, signingKey);
            ps.setBytes(2, X25519Key);
            ps.setString(3, username);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                ps = conn.prepareStatement("SELECT UUID FROM users WHERE username=? LIMIT 1;");
                ps.setString(1, username);
                rs = ps.executeQuery();
                if(rs.next()){
                    return badRequest("Username is already taken.");
                }
                return badRequest("SigningKey or X25519Key already exists. Try again.");
            }

            UUID id = UUID.randomUUID();

            ps = conn.prepareStatement("""
                INSERT INTO users (vault, username, X25519Key, signingKey, nonce, UUID)
                VALUES (?, ?, ?, ?, ?, ?);             
                    """);
            ps.setBytes(1, vault);
            ps.setString(2, username);
            ps.setBytes(3, X25519Key);
            ps.setBytes(4, signingKey);
            ps.setBytes(5, nonce);
            ps.setBytes(6, convertUUIDToBytes(id));

            ps.executeUpdate();            
        } catch (Exception e) {
            // String stackTrace = "";
            // for(StackTraceElement a : e.getStackTrace()){
            //     stackTrace += a.toString() + "\\\n";
            // }
            log.error(e.getMessage());
            e.printStackTrace();
            return error(500, "signupPost Exception: "+e.getMessage());
        }
        return OKRes;
    }
    
}
