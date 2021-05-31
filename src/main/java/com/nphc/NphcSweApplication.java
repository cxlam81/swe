package com.nphc;

import com.nphc.rest.SweRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class NphcSweApplication {
	@Autowired
	SweRestController sweRestController;

	public static void main(String[] args) {
		SpringApplication.run(NphcSweApplication.class, args);
	}

}
