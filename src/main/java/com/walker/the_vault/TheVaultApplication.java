package com.walker.the_vault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TheVaultApplication {

	public static void main(String[] args) {
		SpringApplication.run(TheVaultApplication.class, args);
	}

}
