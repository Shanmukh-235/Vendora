package com.vendora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VendoraApplication {

	public static void main(String[] args) {
		SpringApplication.run(VendoraApplication.class, args);
		System.out.println();
		System.out.println("******************************");
		System.out.println("APPLICATION WORKS SUCCESSFULLY");
		System.out.println("******************************");
	}


	// @Bean
    // public PasswordEncoder passwordEncoder() {
    //     return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
	// }
}
