package net.whynotjava.better_email.emAPI.verify;

import jakarta.annotation.Nullable;

public class VerifyPostJSON {
    private String UUID;
    private String hex;
    public String getUUID() {
        return UUID;
    }
    public void setUUID(String uUID) {
        UUID = uUID;
    }
    public String getHex() {
        return hex;
    }
    public void setHex(String bytes) {
        this.hex = bytes;
    } 
}
