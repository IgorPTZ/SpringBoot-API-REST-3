package api.rest.three.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import api.rest.three.ErrorObject;
import api.rest.three.model.Usuario;
import api.rest.three.repository.UsuarioRepository;
import api.rest.three.service.RecuperacaoDeAcessoService;

@RestController
@RequestMapping(value = "/recuperar")
public class RecuperacaoDeAcessoController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private RecuperacaoDeAcessoService recuperacaoDeAcessoService;
	
	@ResponseBody
	@PostMapping(value="/")
	public ResponseEntity<ErrorObject> recuperarAcesso(@RequestBody Usuario parametros) {
		
		try {
			
			ErrorObject errorObject = new ErrorObject();
			
			Usuario usuario = usuarioRepository.obterUsuarioPorLogin(parametros.getLogin());
			
			if(usuario == null) {
				
				errorObject.setCode("404");
				
				errorObject.setError("Usuario nao encontrado");
			}
			else {
				
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				
				String senhaNova = dateFormat.format(Calendar.getInstance().getTime());
				
				String senhaNovaCriptografada = new BCryptPasswordEncoder().encode(senhaNova);
				
				usuarioRepository.modificarSenha(senhaNovaCriptografada, usuario.getId());
				
				recuperacaoDeAcessoService.enviarEmailDeRecuperacaoDeAcesso("Recuperacao de acesso", 
						                                                    usuario.getLogin(), 
						                                                    "Sua nova senha é: " + senhaNova);
				
				errorObject.setCode("200");
				
				errorObject.setError("Acesso enviado para seu e-mail");
			}
			
			return new ResponseEntity<ErrorObject>(errorObject, HttpStatus.OK);
		}
		catch(Exception e) {
			
			e.printStackTrace();
		}
		
		return null;
	}
}
