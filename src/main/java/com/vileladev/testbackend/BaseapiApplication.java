package com.vileladev.testbackend;

import com.vileladev.testbackend.entities.Usuario;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin(origins = "http://localhost:3000")
@EnableScheduling
public class BaseapiApplication {

	public static void main(String[] args) {

		SpringApplication.run(BaseapiApplication.class, args);
	}

}