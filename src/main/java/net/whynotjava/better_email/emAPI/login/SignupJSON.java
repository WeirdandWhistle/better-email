package net.whynotjava.better_email.emAPI.login;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Base64.Decoder;

import org.slf4j.Logger;

import tools.jackson.databind.ObjectMapper;

public class SignupJSON {
    private String signingKey;
    private String X25519Key;
    private String nonce;
    private String vault;
    private String username;

    public String getSigningKey() {
        return signingKey;
    }
    public void setSigningKey(String signingKey) {
        this.signingKey = signingKey;
    }
    public String getX25519Key() {
        return X25519Key;
    }
    public void setX25519Key(String X25519Key) {
        this.X25519Key = X25519Key;
    }
    public String getVault() {
        return vault;
    }
    public void setVault(String vault) {
        this.vault = vault;
    }
    public String getNonce(){
        return nonce;
    }
    public void setNonce(String nonce){
        this.nonce = nonce;
    }
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public void log(Logger log){
        ObjectMapper mapper = new ObjectMapper();
        log.info(mapper.writeValueAsString(this));
    }
    public static SignupJSON JSONFromDB(ResultSet rs) throws SQLException{
        SignupJSON out = new SignupJSON();
        Decoder d = Base64.getUrlDecoder();
        out.X25519Key = d.decode(rs.getBytes("X25519Key"));
        out.signingKey = d.decode(rs.getBytes("signingKey"));
        out.nonce = d.decode(rs.getBytes("nonce"));
        out.vault = d.decode(rs.getBytes("vault"));
        out.username = rs.getString("username");
        return out;
    }
} 
