package net.whynotjava.better_email.emAPI;

public class Username {

    public static final String allowedCharset = "abcdefghijklmnopqrstuvwxyz1234567890.-_";

    public static boolean validateUsername(String username){
        if("1234567890.-_".contains(username.subSequence(0, 1))){
            return false;
        }
        for(int i = 0; i < username.length(); i++){
            if(!allowedCharset.contains(username.subSequence(i, i+1))){
                
                return false;
            }
        }
        return true;
    }
    
}
