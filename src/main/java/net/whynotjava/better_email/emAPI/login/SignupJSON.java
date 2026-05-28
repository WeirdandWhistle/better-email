public class SignupJSON {
    private String signingKey;
    private String X25519Key;
    private String nonce;
    private String vault;

    public String getSigningKey() {
        return signingKey;
    }
    public void setSigningKey(String signingKey) {
        this.signingKey = signingKey;
    }
    public String getX25519Key() {
        return X25519Key;
    }
    public void setX25519Key(String x25519Key) {
        X25519Key = x25519Key;
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
} 
