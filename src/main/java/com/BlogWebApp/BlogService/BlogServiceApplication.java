package com.BlogWebApp.BlogService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.BlogWebApp.BlogService.client")
public class BlogServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogServiceApplication.class, args);
	}

}
