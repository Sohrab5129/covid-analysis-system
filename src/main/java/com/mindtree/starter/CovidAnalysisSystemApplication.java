package com.mindtree.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = { "com.mindtree.*" })
@ComponentScan(basePackages = { "com.mindtree.*" })
@EntityScan("com.mindtree.*")
public class CovidAnalysisSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(CovidAnalysisSystemApplication.class, args);
	}
}
