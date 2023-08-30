package com.blogchallenge.blogchallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BlogChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogChallengeApplication.class, args);
	}

}
