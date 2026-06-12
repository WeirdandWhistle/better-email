package net.whynotjava.better_email.emAPI.message;

import static net.whynotjava.better_email.Constants.CHALLENGE_NONCE_LENGTH;
import static net.whynotjava.better_email.Constants.HASH_LENGNTH;
import static net.whynotjava.better_email.Constants.KEY_LENGTH;
import static net.whynotjava.better_email.Constants.MAX_MESSAGE_LENGTH;
import static net.whynotjava.better_email.Constants.NONCE_LENGTH;
import static net.whynotjava.better_email.Util.CacheControl;
import static net.whynotjava.better_email.Util.badRequest;
import static net.whynotjava.better_email.Util.error;
import static net.whynotjava.better_email.Util.noCache;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.whynotjava.better_email.Database;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

@RestController
public class Message {

    @Autowired
    Database db;

    Logger log = LoggerFactory.getLogger(getClass());

    @PostMapping("/emapi/v1/message")
    public ResponseEntity<?> postMessage(@RequestBody MessagePostJSON json){
       try (Connection conn = db.getDB().getConnection()){   
            byte nonce[] = json.getNonceBytes();
            byte targetX25519PublicKey[] = json.getTargetX25519PublicKeyBytes();
            byte tempPublicX25519Key[] = json.getTempPublicX25519KeyBytes();
            byte challengeNonce[] = json.getChallengeNonceBytes();
            byte encryptedMessage[] = json.getEncryptedMessageBytes();

            // enfore lengths
            if(nonce.length != NONCE_LENGTH) return badRequest("Nonce is the wrong length.");
            if(targetX25519PublicKey.length != KEY_LENGTH) return badRequest("targetX25519PublicKey is the wrong length.");
            if(tempPublicX25519Key.length != KEY_LENGTH) return badRequest("tempPublicX25519Key is the wrong length.");
            if(challengeNonce.length != CHALLENGE_NONCE_LENGTH) return badRequest("challengeNonce is the wrong length.");
            if(encryptedMessage.length > MAX_MESSAGE_LENGTH) return badRequest("encryptedMessgae is too long.");

            // make sure target exists
            PreparedStatement ps = conn.prepareStatement("SELECT nonce FROM users WHERE X25519Key=? LIMIT 1;");
            ps.setBytes(1, targetX25519PublicKey);
            ResultSet rs = ps.executeQuery();
            if(!rs.next()){
                return badRequest("Target user does not exist");
            }

            // write
            ps = conn.prepareStatement("""
                INSERT INTO message (nonce, targetX25519PublicKey, tempPublicX25519Key, challengeNonce, encryptedMessage)                   
                VALUES (?, ?, ?, ?, ?);        
            """);
            ps.setBytes(1, nonce);
            ps.setBytes(2, targetX25519PublicKey);
            ps.setBytes(3, tempPublicX25519Key);
            ps.setBytes(4, challengeNonce);
            ps.setBytes(5, encryptedMessage);
            ps.executeUpdate();

            // return id
            ps = conn.prepareStatement("SELECT id FROM message WHERE nonce=? AND challengeNonce=? AND targetX25519PublicKey=? LIMIT 1;");
            ps.setBytes(1, nonce);
            ps.setBytes(2, challengeNonce);
            ps.setBytes(3, targetX25519PublicKey);
            rs = ps.executeQuery();
            if(!rs.next()){
                log.error("could not find id of created message!");
            }
            String hexID = HexFormat.of().toHexDigits(rs.getLong("id"));
            ObjectNode root = JsonMapper.builder().build().createObjectNode();
            root.put("id", hexID);
            root.put("status",200);
            root.put("encoding","hex");
            return new ResponseEntity<>(root.toString(), noCache(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return error(500, "Post Message Excpetion: "+e.getMessage());
        }
    }
    
    @GetMapping("/emapi/v1/message")
    public ResponseEntity<?> getMessage(
        @RequestParam(required = false) String id,
        @RequestParam(required = false) String encoding,
        @RequestParam(required = false) String key){
        try (Connection conn = db.getDB().getConnection()){
            PreparedStatement ps;
            
            if(id != null){
                long realID = -1;
                switch (encoding) {
                    case "base10":
                        realID = Long.parseLong(id);
                        break;
                    case "base64":
                        realID = ByteBuffer.wrap(Base64.getUrlDecoder().decode(id)).getLong();
                        break;
                    default:
                        realID = HexFormat.fromHexDigitsToLong(id);
                        break;
                }
                if(realID == -1){
                    return badRequest("could not decode id.");
                }
                ps = conn.prepareStatement("SELECT * FROM message WHERE id=? LIMIT 1;");
                ps.setLong(1, realID);
            } else if (key != null){
                if(encoding != null && !encoding.equals("base64")){
                    return badRequest("only offered decoding for key is base64.");
                }
                byte X25519Key[] = Base64.getUrlDecoder().decode(key);
                ps = conn.prepareStatement("SELECT * FROM message WHERE targetX25519PublicKey=?;");
                ps.setBytes(1, X25519Key);
            } else {
                return badRequest("nothing to look up.");
            }
            
            Encoder e = Base64.getUrlEncoder();
            ResultSet rs = ps.executeQuery();
            ObjectNode root = JsonMapper.builder().build().createObjectNode();
            root.put("id", HexFormat.of().toHexDigits(rs.getLong("id")));
            root.put("nonce", e.encode(rs.getBytes("nonce")));
            root.put("targetX25519PublicKey", e.encode(rs.getBytes("targetX25519PublicKey")));
            root.put("tempPublicX25519Key", e.encode(rs.getBytes("tempPublicX25519Key")));
            root.put("challengeNonce", e.encode(rs.getBytes("challengeNonce")));
            root.put("encryptedMessage", e.encode(rs.getBytes("encryptedMessage")));
            root.put("status",200);
            return new ResponseEntity<>(root.toString(), CacheControl("public"), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return error(500, "Get Message Excpetion: "+e.getMessage());
        }
    }
}
