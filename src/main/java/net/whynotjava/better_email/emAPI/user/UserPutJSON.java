package net.whynotjava.better_email.emAPI.user;

import java.util.HexFormat;

public class UserPutJSON {
    private String type;
    private String value;
    private String signature;
    private String challenge;
    public String getChallenge() {
        return challenge;
    }
    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }
    public byte[] getChallengeBytes(){
        return HexFormat.of().parseHex(challenge);
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getSignature() {
        return signature;
    }
    public void setSignature(String signature) {
        this.signature = signature;
    }
    public byte[] getSignatureBytes(){
        return HexFormat.of().parseHex(signature);
    }
}
