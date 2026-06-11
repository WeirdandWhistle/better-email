package net.whynotjava.better_email;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.springframework.http.*;

public class Util {
    
    public static final ResponseEntity<String> OKRes = new ResponseEntity<>("{\"status\":200,\"error\":\"ok\"}", noCache(), HttpStatus.OK);
    public static ResponseEntity<String> error(int status, String error){
        return new ResponseEntity<>("{\"status\":"+status+",\"error\":\""+error.replace("\"", "\\\"")+"\"}", noCache(), HttpStatusCode.valueOf(status));
    }
    public static ResponseEntity<String> error(int status, String error, String stackTrace){
        return new ResponseEntity<>("{\"status\":"+status+",\"error\":\""+error.replace("\"", "\\\"")+"\",\"stackTrace\":\""+stackTrace.replace("\"", "\\\"")+"\"}", noCache(), HttpStatusCode.valueOf(status));
    }
    public static ResponseEntity<String> badRequest(String error){
        return new ResponseEntity<>("{\"status\":400,\"error\":\""+error+"\"}", noCache(), HttpStatus.BAD_REQUEST);
    }
    public static HttpHeaders noCache(){
        final HttpHeaders noCache = new HttpHeaders();
        noCache.add("Cache-Control", "no-store");
        return noCache;
    }
    public static HttpHeaders CacheControl(String s){
        final HttpHeaders c = new HttpHeaders();
        c.add("Cache-Control", s);
        return c;
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
    public static byte[] concatArr(byte[] a, byte[] b){
        byte c[] = new byte[a.length + b.length];
        for(int i = 0; i < c.length; i++){
            c[i] = i < a.length ? a[i] : b[i - a.length];
        }
        return c;
    }
}
