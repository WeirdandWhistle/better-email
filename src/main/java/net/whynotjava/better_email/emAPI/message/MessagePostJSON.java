package net.whynotjava.better_email.emAPI.message;

import java.util.Base64;

public class MessagePostJSON {
    private String nonce;
    private String targetX25519PublicKey;
    private String tempPublicX25519Key;
    private String challengeNonce;
    private String encryptedMessage;
    public String getNonce() {
        return nonce;
    }
    public byte[] getNonceBytes() {
        return Base64.getUrlDecoder().decode(nonce);
    }
    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
    public String getTargetX25519PublicKey() {
        return targetX25519PublicKey;
    }
    public byte[] getTargetX25519PublicKeyBytes() {
        return Base64.getUrlDecoder().decode(targetX25519PublicKey);
    }
    public void setTargetX25519PublicKey(String targetX25519PublicKey) {
        this.targetX25519PublicKey = targetX25519PublicKey;
    }
    public String getTempPublicX25519Key() {
        return tempPublicX25519Key;
    }
    public byte[] getTempPublicX25519KeyBytes() {
        return Base64.getUrlDecoder().decode(tempPublicX25519Key);
    }
    public void setTempPublicX25519Key(String tempPublicX25519Key) {
        this.tempPublicX25519Key = tempPublicX25519Key;
    }
    public String getChallengeNonce() {
        return challengeNonce;
    }
    public byte[] getChallengeNonceBytes() {
        return Base64.getUrlDecoder().decode(challengeNonce);
    }
    public void setChallengeNonce(String challengeNonce) {
        this.challengeNonce = challengeNonce;
    }
    public String getEncryptedMessage() {
        return encryptedMessage;
    }
    public byte[] getEncryptedMessageBytes() {
        return Base64.getUrlDecoder().decode(encryptedMessage);
    }
    public void setEncryptedMessage(String encryptedMessage) {
        this.encryptedMessage = encryptedMessage;
    }
}
