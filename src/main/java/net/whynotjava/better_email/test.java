package net.whynotjava.better_email;

import javax.sql.*;
import java.sql.*;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;
import net.whynotjava.better_email.Database;

@RestController
public class test {
    
    @Autowired
    Database db;

    @GetMapping("/db")
    public String db(){
        try {

        Connection conn = db.getDB().getConnection();

		Statement create_table = conn.createStatement();

		create_table.executeUpdate("CREATE TABLE IF NOT EXISTS test (id INT, name TEXT);");

		conn.close();
        } catch (SQLException e){
            e.printStackTrace();
            return "sql error:"+e.getMessage();
        }

        return "done!";
    }
}
