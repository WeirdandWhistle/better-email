package net.whynotjava.better_email;

public class Constants {
    public static final String DB_URL = "jdbc:sqlite:database.db";
    public static final int KEY_LENGTH = 32;
    public static final int SINGING_KEY_LENGTH = 32;
    public static final int NONCE_LENGTH = 12;
    public static final int VAULT_LENGTH = 16 * 1000;
    public static final int MAX_USERNAME_LENGTH = 25;
    public static final int UUID_TEXT_LENGTH = 36;
    public static final int HASH_LENGNTH = 32;
    public static final int CHALLENGE_LENGTH = 16;
    public static final int CHALLENGE_MAX_LIFE = 60;
    public static final String HASH_ALG = "SHA-256";

    // CacheControl
    public class CC{
        public static final int USER_SECONDS = 7;
    }
    
}