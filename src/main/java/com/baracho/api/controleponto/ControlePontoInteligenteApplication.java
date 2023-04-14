package com.baracho.api.controleponto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication (exclude = { SecurityAutoConfiguration.class })
public class ControlePontoInteligenteApplication {

	public static void main(String[] args) {
		SpringApplication.run(ControlePontoInteligenteApplication.class, args);
	}

}
