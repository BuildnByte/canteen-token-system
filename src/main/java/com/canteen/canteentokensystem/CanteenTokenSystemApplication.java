package com.canteen.canteentokensystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class CanteenTokenSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(CanteenTokenSystemApplication.class, args);
	}

}
