package com.BlogWebApp.BlogService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.BlogWebApp.Common.client")
@SpringBootApplication(scanBasePackages = {"com.BlogWebApp.BlogService", "com.BlogWebApp.Common.security"})
public class BlogServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogServiceApplication.class, args);
	}

}
