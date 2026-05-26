package com.digitaltravel.erp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DigitalTravelErpApplication {

	public static void main(String[] args) {
		SpringApplication.run(DigitalTravelErpApplication.class, args);
	}

}
