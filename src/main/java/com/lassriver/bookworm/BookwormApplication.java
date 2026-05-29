package com.lassriver.bookworm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BookwormApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(BookwormApplication.class);

		// Interceptor: Lee las variables JUSTO ANTES de conectar a la base de datos
		app.addInitializers(context -> {
			System.out.println("\n\n====== DIAGNÓSTICO PROFUNDO DE SPRING BOOT ======");
			System.out.println("DB_URL leída:      [" + context.getEnvironment().getProperty("DB_URL") + "]");
			System.out.println("DB_USERNAME leída: [" + context.getEnvironment().getProperty("DB_USERNAME") + "]");
			System.out.println("DB_PASSWORD leída: [" + context.getEnvironment().getProperty("DB_PASSWORD") + "]");
			System.out.println("===================================================\n\n");
		});

		app.run(args);
	}

}
