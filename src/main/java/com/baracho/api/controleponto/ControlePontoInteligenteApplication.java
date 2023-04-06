package com.baracho.api.controleponto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication //(exclude = { SecurityAutoConfiguration.class })
public class ControlePontoInteligenteApplication {

	public static void main(String[] args) {
		SpringApplication.run(ControlePontoInteligenteApplication.class, args);
	}

}
