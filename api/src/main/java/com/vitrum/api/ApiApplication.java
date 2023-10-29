package com.vitrum.api;

import com.vitrum.api.credentials.authentication.AuthService;
import com.vitrum.api.credentials.user.UserService;
import com.vitrum.api.dto.Request.AuthenticationRequest;
import com.vitrum.api.dto.Request.RegisterRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner (
			UserService userService,
			AuthService authService
	) {
		return args -> {
			var admin = RegisterRequest.builder()
					.username("AVitrum")
					.email("andrey.almashi@gmail.com")
					.password("qwertY12")
					.role("ADMIN")
					.build();
			userService.create(admin);
			System.out.println("Token: " + authService.authenticate(
					AuthenticationRequest.builder()
							.username("AVitrum")
							.password("qwertY12")
							.build()
			).getAccessToken());
		};
	}

}
