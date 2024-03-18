package it.epicode.capstoneproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class CapstoneprojectApplication {

	private static final Logger logger = LoggerFactory.getLogger(CapstoneprojectApplication.class);

	public static void main(String[] args) {
		logger.info("Starting CapstoneprojectApplication...");
		SpringApplication.run(CapstoneprojectApplication.class, args);
		logger.info("CapstoneprojectApplication started successfully.");
	}
}
