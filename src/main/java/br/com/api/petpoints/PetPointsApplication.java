package br.com.api.petpoints;

import br.com.api.petpoints.core.api.StripeProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
@EnableConfigurationProperties(StripeProperties.class)
public class PetPointsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetPointsApplication.class, args);
	}

}
