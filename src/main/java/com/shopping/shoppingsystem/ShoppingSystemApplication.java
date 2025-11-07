package com.shopping.shoppingsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShoppingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoppingSystemApplication.class, args);

		// Signaling the start of the application in the console
		System.out.println("System started successfully!");
		System.out.println("Access: http://localhost:8080");
	}

}
