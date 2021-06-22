package api.rest.three.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import api.rest.three.ErrorObject;
import api.rest.three.model.Usuario;
import api.rest.three.repository.UsuarioRepository;

@RestController
@RequestMapping(value = "/recuperar")
public class RecuperacaoDeAcessoController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@ResponseBody
	@PostMapping(value="/")
	public ResponseEntity<ErrorObject> recuperarAcesso(@RequestBody Usuario parametros) {
		
		ErrorObject errorObject = new ErrorObject();
		
		Usuario usuario = usuarioRepository.obterUsuarioPorLogin(parametros.getLogin());
		
		if(usuario == null) {
			
			errorObject.setCode("404");
			
			errorObject.setError("Usuario nao encontrado");
		}
		else {
			
			errorObject.setCode("200");
			
			errorObject.setError("Acesso enviado para seu e-mail");
		}
		
		return new ResponseEntity<ErrorObject>(errorObject, HttpStatus.OK);
	}
}
