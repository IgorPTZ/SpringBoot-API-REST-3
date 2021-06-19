package api.rest.three.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import api.rest.three.ErrorObject;
import api.rest.three.repository.UsuarioRepository;

@RestController
@RequestMapping(value = "/recuperacao-de-acesso")
public class RecuperacaoDeAcessoController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	public ResponseEntity<ErrorObject> recuperarAcesso() {
		
		return null;
	}
}
