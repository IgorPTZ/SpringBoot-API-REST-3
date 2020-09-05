package curso.api.rest.security;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {
	
	/* Tempo de validade do token */
	private static final long EXPIRATION_TIME = 172800000;
	
	/* Uma senha unica para compor a autenticacao e ajudar na segurança */
	private static final String SECRET = "SenhaExtremamenteSecreta";
	
	/* Prefixo padrão do token */
	private static final String TOKEN_PREFIX = "Bearer";
	
	/* Identificação do cabeçalho da resposta */
	private static final String HEADER_STRING = "Authorization";
	
	
	/* Gerando token de autenticacao e adicionando ao cabeçalho e resposta Http */
	public void addAuthentication(HttpServletResponse response, String username) throws Exception {
		
		String JWT = Jwts.builder(). /* Chama o gerador de token */
				setSubject(username). /* Adiciona o usuario */
				setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) /* Tempo de expiracao */
				.signWith(SignatureAlgorithm.HS512, SECRET) /* Aplicação de algoritmo de geracao de senha e compactacao */
				.compact();
		
		/* Junta o token com o prefixo */
		String token = TOKEN_PREFIX + " " + JWT; 
		
		/* Adiciona no cabeçalho http */
		response.addHeader(HEADER_STRING, token); /* Ex: Authorization: Bearer token */
		
		/* Escreve token como resposta no corpo http */
		response.getWriter().write("{\"Authorization\": \"" + token + "\"}");
	}
	
	
	/* Retorna o usuario validado com token ou caso nao seja valido retorna null */
	public Authentication getAuthentication(HttpServletRequest request) {
		
		/* Obtem o token enviado no cabeçalho http */
		
		String token = request.getHeader(HEADER_STRING);
		
		if(token != null) {
			
			String user = Jwts.parser().setSigningKey(SECRET)
						  .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
						  .getBody().getSubject();
			
			if(user != null) {
				
			}
			else {
				return null; /* Retorno para usuario nao autorizado */
			}
		}
		else {
			return null; /* Retorno para usuario nao autorizado */
		}
	}
	
}
