package br.com.carteiradigital;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Carteira Digital", version = "1", description = "Api para eventos de transações da carteira digital"))
public class CarteiraDigitalApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarteiraDigitalApplication.class, args);
	}

}
