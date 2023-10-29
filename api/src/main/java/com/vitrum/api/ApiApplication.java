package com.vitrum.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner commandLineRunner (
//			UserService userService,
//			AuthService authService
//	) {
//		return args -> {
//			var admin = RegisterRequest.builder()
//					.username("AVitrum")
//					.email("andrey.almashi@gmail.com")
//					.password("qwertY12")
//					.role("ADMIN")
//					.build();
//			userService.create(admin);
//			System.out.println("Token: " + authService.authenticate(
//					AuthenticationRequest.builder()
//							.username("AVitrum")
//							.password("qwertY12")
//							.build()
//			).getAccessToken());
//		};
//	}

}
