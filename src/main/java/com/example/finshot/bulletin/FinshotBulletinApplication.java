package com.example.finshot.bulletin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FinshotBulletinApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinshotBulletinApplication.class, args);
	}

}
