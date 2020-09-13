package curso.api.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import curso.api.rest.service.ImplementacaoUserDetailsService;


/* Mapeia URL's e endereços. Também autoriza ou bloqueia acessos a URL's */

@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
	
	/* Configura as solicitações de acesso por Http */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		/* Ativando a proteção contra usuário que não está validados por token */
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		
		/* Ativando a permissão para acesso a pagina inicial do sistema. Ex: sistema.com.br/index.html */
		.disable().authorizeRequests().antMatchers("/").permitAll() 
		.antMatchers("/index").permitAll()
		
		/* Url de Logout - Redireciona apos o usuario deslogar do sistema */
		.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")
		
		/* Mapeia URL de logout e invalida o usuario */
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		
		/* Filtra a requisição de login para autenticação */
		.and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)
		
		/* Filtra demais requisições para verificar a presença do token JWT no header http */
		.addFilterBefore(new JwtApiAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class);
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		/* Service que irá consultar o usuario no banco de dados */
		auth.userDetailsService(implementacaoUserDetailsService)
		.passwordEncoder(new BCryptPasswordEncoder()); /* Padrão para codificação de senha */
	}
}
