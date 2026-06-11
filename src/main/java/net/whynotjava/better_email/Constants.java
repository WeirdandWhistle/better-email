package net.whynotjava.better_email;

public class Constants {
    public static final String DB_URL = "jdbc:sqlite:database.db";
    public static final int KEY_LENGTH = 32;
    public static final int SINGING_KEY_LENGTH = 32;
    public static final int NONCE_LENGTH = 12;
    public static final int VAULT_LENGTH = 16 * 1000;
    public static final int MAX_USERNAME_LENGTH = 25;

    // CacheControl
    public class CC{
        public static final int USER_SECONDS = 7;
    }
    
}