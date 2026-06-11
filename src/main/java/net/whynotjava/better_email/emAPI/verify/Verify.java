package net.whynotjava.better_email.emAPI.verify;

import static net.whynotjava.better_email.Util.OKRes;
import static net.whynotjava.better_email.Util.badRequest;
import static net.whynotjava.better_email.Util.concatArr;
import static net.whynotjava.better_email.Util.convertUUIDToBytes;
import static net.whynotjava.better_email.Util.error;
import static net.whynotjava.better_email.Util.noCache;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HexFormat;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import net.whynotjava.better_email.Database;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

import static net.whynotjava.better_email.Constants.*;

@RestController
public class Verify {
    
    @Autowired
    Database db;

    Logger log = LoggerFactory.getLogger(getClass());

    @PostMapping("/emapi/v1/verify")
    public ResponseEntity<String> verifyPost(@RequestBody VerifyPostJSON json){
        try (Connection conn = db.getDB().getConnection()){
            cleanDB(conn);
            if(json.getUUID().length() != UUID_TEXT_LENGTH){
                return badRequest("UUID is the wrong length");
            }
            byte hex[] = HexFormat.of().parseHex(json.getHex());
            if(hex.length != HASH_LENGNTH/2){
                return badRequest("hex must be half of hash length");
            }

            byte uuid[] = convertUUIDToBytes(UUID.fromString(json.getUUID()));
            PreparedStatement ps = conn.prepareStatement("SELECT nonce FROM users WHERE UUID=? LIMIT 1;");
            ps.setBytes(1, uuid);
            ResultSet rs = ps.executeQuery();
            if(!rs.next()){
                return badRequest("UUID does not exist.");
            }

            ps = conn.prepareStatement("DELETE FROM verifyUser WHERE UUID=?;");
            ps.setBytes(1, uuid);
            ps.executeUpdate();

            SecureRandom sr = new SecureRandom();
            byte rand[] = new byte[HASH_LENGNTH/2];
            sr.nextBytes(rand);

            byte concat[] = concatArr(hex, rand);

            byte challenge[] = new byte[CHALLENGE_LENGTH];
            sr.nextBytes(challenge);

            MessageDigest hash = MessageDigest.getInstance(HASH_ALG);
            byte value[] = hash.digest(concat);

            long death = (System.currentTimeMillis() / 1000) + CHALLENGE_MAX_LIFE;

            ps = conn.prepareStatement("INSERT INTO verifyUser (death, UUID, challenge, value) VALUES (?, ?, ?, ?);");
            ps.setLong(1, death);
            ps.setBytes(2, uuid);
            ps.setBytes(3, challenge);
            ps.setBytes(4, value);
            ps.executeUpdate();

            ObjectNode root = JsonMapper.builder().build().createObjectNode();
            root.put("status", 200);
            root.put("challenge", HexFormat.of().formatHex(challenge));
            root.put("rand", HexFormat.of().formatHex(rand));
            root.put("encoding", "hex");
            
            return new ResponseEntity<>(root.toString(), noCache(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return error(500, "verifyPost Exception: "+e.getMessage());
        }
    }

    public static void cleanDB(Connection conn) throws Exception{
        PreparedStatement ps = conn.prepareStatement("DELETE FROM verifyUser WHERE death<?;");
        long now = System.currentTimeMillis() / 1000;
        ps.setLong(1, now);
        ps.executeUpdate();        
    }
}
