package curso.api.rest.security;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class JWTTokenAutenticacaoService {
	
	/* Tempo de validade do token */
	private static final long EXPIRATION_TIME = 172800000;
	
	/* Uma senha unica para compor a autenticacao e ajudar na segurança */
	private static final String SECRET = "*SenhaExtremamenteSecreta";
	
	/* Prefixo padrão do token */
	private static final String TOKEN_PREFIX = "Bearer";
	
	/* Identificação do cabeçalho da resposta */
	private static final String HEADER_STRING = "Authorization";
	
	/* Gerando token de autenticacao e adicionando ao cabeçalho e resposta Http */
	public void addAuthentication(HttpServletResponse response, String username) throws Exception {
		
		String JWT
	}
}
