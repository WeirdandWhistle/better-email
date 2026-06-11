package net.whynotjava.better_email;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Database{

    @Autowired
    private DataSource dataSource;
    private Logger log = LoggerFactory.getLogger(getClass());     
    private static boolean init = false;

    public DataSource getDB(){
        if(!init){
            init = true;
            initDB();            
        }
        return dataSource;
    }
    public void initDB(){
        try (Connection conn = getDB().getConnection()){
            conn.createStatement().executeUpdate("""
                CREATE TABLE IF NOT EXISTS users 
                (vault VARBINARY(16000), username TEXT, displayName TEXT, X25519Key BINARY(32), signingKey BINARY(32), nonce BINARY(12), UUID BINARY(16));
                """);
            conn.createStatement().executeUpdate("""
                CREATE TABLE IF NOT EXISTS verifyUser
                (death BIGINT, UUID BINARY(16), challenge BINARY(16), value BINARY(32));
                """);
        } catch (SQLException e) {
            Logger log = LoggerFactory.getLogger(getClass());
            log.error("While initing DB: "+e.getMessage());
            System.exit(1);
        }
    }
}