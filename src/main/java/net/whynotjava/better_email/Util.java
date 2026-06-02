package net.whynotjava.better_email;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class Util {
    public static final ResponseEntity<String> OKRes = new ResponseEntity<>("{\"status\":200,\"error\":\"ok\"}",HttpStatus.OK);
    public static ResponseEntity<String> error(int status, String error){
        return new ResponseEntity<>("{\"status\":"+status+",\"error\":\""+error+"\"}",HttpStatusCode.valueOf(status));
    }
    public static ResponseEntity<String> badRequest(String error){
        return new ResponseEntity<>("{\"status\":400,\"error\":\""+error+"\"}",HttpStatus.BAD_REQUEST);
    }

    public static byte[] convertUUIDToBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
    public static UUID convertBytesToUUID(byte[] bytes) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
    long high = byteBuffer.getLong();
    long low = byteBuffer.getLong();
    return new UUID(high, low);
}
}
