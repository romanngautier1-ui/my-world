package com.app.myworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MyworldApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyworldApplication.class, args);
	}

}
