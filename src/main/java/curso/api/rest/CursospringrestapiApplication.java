package curso.api.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EntityScan(basePackages = {"curso.api.rest.model"})
@ComponentScan(basePackages = {"curso.*"})
@EnableJpaRepositories(basePackages = {"curso.api.rest.repository"})
@EnableTransactionManagement
@EnableWebMvc
@RestController
@EnableAutoConfiguration
@EnableCaching
public class CursospringrestapiApplication implements WebMvcConfigurer{

	public static void main(String[] args) {
		SpringApplication.run(CursospringrestapiApplication.class, args);
		//System.out.println(new BCryptPasswordEncoder().encode("123"));
		//System.out.println(new BCryptPasswordEncoder().encode("321"));
	}
	
	
	/* Utilizando CORS para fazer o mapeamento de rotas global da aplicação */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		
		/* Utilizando o CORS para liberar apenas requisições POST, PUT e GET para o cliente localhost:8080 */
		registry.addMapping("/usuario/**").
		allowedMethods("POST", "PUT", "GET"). 
		allowedOrigins("*");
	}
}
