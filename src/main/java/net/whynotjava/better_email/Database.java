package net.whynotjava.better_email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;

@Service
public class Database{

    @Autowired
    private DataSource dataSource;

    public DataSource getDB(){
        return dataSource;
    }
}