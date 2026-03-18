package br.ufma.lsdi.SmartMeterVerifierSSI;

import br.ufma.lsdi.SmartMeterVerifierSSI.configs.AuthenticationRefreshProperties;
import br.ufma.lsdi.SmartMeterVerifierSSI.configs.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties({
        JwtProperties.class,
        AuthenticationRefreshProperties.class
})
@EnableScheduling
public class SmartMeterVerifierSsiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartMeterVerifierSsiApplication.class, args);
	}

}
