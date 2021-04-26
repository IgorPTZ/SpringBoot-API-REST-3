package api.rest.three.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import api.rest.three.ApplicationContextLoad;
import api.rest.three.model.Usuario;
import api.rest.three.repository.UsuarioRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {
	
	/* Tempo de validade do token - 2 dias */
	private static final long EXPIRATION_TIME = 172800000;
	
	/* Uma senha unica para compor a autenticacao e ajudar na segurança */
	private static final String SECRET = "SenhaExtremamenteSecreta";
	
	/* Prefixo padrão do token */
	private static final String TOKEN_PREFIX = "Bearer";
	
	/* Identificação do cabeçalho da resposta */
	private static final String HEADER_STRING = "Authorization";
	
	
	/* Gerando token de autenticacao e adicionando ao cabeçalho e resposta Http */
	public void addAuthentication(HttpServletResponse response, String username) throws IOException {
		
		String JWT = Jwts.builder(). /* Chama o gerador de token */
				setSubject(username). /* Adiciona o usuario */
				setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) /* Tempo de expiracao */
				.signWith(SignatureAlgorithm.HS512, SECRET) /* Aplicação de algoritmo de geracao de senha e compactacao */
				.compact();
		
		/* Junta o token com o prefixo */
		String token = TOKEN_PREFIX + " " + JWT; 
		
		/* Adiciona no cabeçalho http */
		response.addHeader(HEADER_STRING, token); /* Ex: Authorization: Bearer token */
		
		/* Para atualizar o token presente na tabela de usuario durante um novo login */
		ApplicationContextLoad.getApplicationContext()
		.getBean(UsuarioRepository.class).atualizarTokenUser(JWT, username);
		
		/* Liberando resposta para porta diferentes da que a API utiliza (Ex: request vinda do browser) */
		liberarCors(response);
		
		/* Escreve token como resposta no corpo http */
		response.getWriter().write("{\"Authorization\": \"" + token + "\"}");
	}
	
	
	/* Retorna o usuario validado com token ou caso nao seja valido retorna null */
	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {
		
		/* Obtem o token enviado no cabeçalho http */
		String token = request.getHeader(HEADER_STRING);
		
		try {
			if(token != null) {
				
				String tokenLimpo = token.replace(TOKEN_PREFIX, "").trim();
				
				/* Faz a validação do token do usuario na requisicao */
				String user = Jwts.parser()
						      .setSigningKey(SECRET)
							  .parseClaimsJws(tokenLimpo)
							  .getBody()
							  .getSubject();
				
				if(user != null) {
					
					Usuario usuario = ApplicationContextLoad
							          .getApplicationContext()
									  .getBean(UsuarioRepository.class)
									  .findUserByLogin(user);
					
					if(usuario != null) {
						
						if(tokenLimpo.equalsIgnoreCase(usuario.getToken())) {
							return new UsernamePasswordAuthenticationToken(
									   usuario.getLogin(), 
								       usuario.getSenha(), 
								       usuario.getAuthorities());
						}
					
					}

				}
			} // Fim da condicao
		}
		catch(ExpiredJwtException e) {
			
			try {
				
				response.getOutputStream().print("Token expirado! Faca o login novamente.");
			} catch (IOException e1) {}
		}

		
		liberarCors(response);
		
		return null; /* Retorno para usuario nao autorizado */
	}


	private void liberarCors(HttpServletResponse response) {
		
		if(response.getHeader("Access-Control-Allow-Origin") == null) {
			
			response.addHeader("Access-Control-Allow-Origin", "*");
		}
		
		if(response.getHeader("Access-Control-Allow-Headers") == null) {
			
			response.addHeader("Access-Control-Allow-Headers", "*");
		}
		
		if(response.getHeader("Access-Control-Request-Headers") == null) {
			
			response.addHeader("Access-Control-Request-Headers", "*");
		}
		
		if(response.getHeader("Access-Control-Allow-Methods") == null) {
			
			response.addHeader("Access-Control-Allow-Methods", "*");
		}
	}
	
}
