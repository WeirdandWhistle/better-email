package net.whynotjava.better_email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;

import javax.sql.*;

@SpringBootApplication
public class BetterEmailApplication {

	

	public static void main(String[] args) {
		SpringApplication.run(BetterEmailApplication.class, args);

		
	}

}
