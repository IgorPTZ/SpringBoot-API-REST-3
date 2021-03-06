package api.rest.three.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import api.rest.three.model.DadosDoGrafico;
import api.rest.three.model.Relatorio;
import api.rest.three.model.Usuario;
import api.rest.three.model.UsuarioDTO;
import api.rest.three.repository.TelefoneRepository;
import api.rest.three.repository.UsuarioRepository;
import api.rest.three.service.ImplementacaoUserDetailsService;
import api.rest.three.service.RelatorioService;

/* Arquitetura REST */

@RestController
@RequestMapping(value = "/usuario")
public class UsuarioController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
	@Autowired
	private RelatorioService relatorioService;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/* Serviço RESTful */
	
	/* Versionamento de API */
	@GetMapping(value = "v1/{id}", produces = "application/json")
	
	/* O CacheEvict  remove todos os caches antigos que nao sao mais atualizados e utilizados */
	@CacheEvict(value="cache-obter-usuario-v1", allEntries = true)
	
	/* O CachePut identifica atualizacoes e adiciona ao cache */
	@CachePut("cache-obter-usuario-v1")
	public ResponseEntity<Usuario> obterUsuarioV1(@PathVariable (value = "id") Long id) {
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		System.out.println("Old version, for most clients");

		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}
	
	/* Versionamento de API */
	@GetMapping(value = "v2/{id}", produces = "application/json")
	@CacheEvict(value="cache-obter-usuario-v2", allEntries = true)
	@CachePut("cache-obter-usuario-v2")
	public ResponseEntity<UsuarioDTO> obterUsuarioV2(@PathVariable (value = "id") Long id) {
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		System.out.println("New version, only some clients");

		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
	}
	
	
	@GetMapping(value="/", produces = "application/json")
	@CacheEvict(value="cache-obter-usuario", allEntries = true)
	@CachePut("cache-obter-usuario")
	public ResponseEntity<List<Usuario>> obterUsuarios() {
				
		List<Usuario> usuarios = usuarioRepository.findAll(Sort.by("nome"));
		
		return new ResponseEntity<List<Usuario>>(usuarios, HttpStatus.OK);
	}
	
	@GetMapping(value="/pagina/{pagina}", produces = "application/json")
	@CacheEvict(value="cache-obter-usuarios-paginados", allEntries = true)
	@CachePut("cache-obter-usuarios-paginados")
	public ResponseEntity<Page<Usuario>> obterUsuariosPaginados(@PathVariable(value = "pagina") Long pagina){
		
		Page<Usuario> usuarios = usuarioRepository.findAll(PageRequest.of(pagina.intValue(), 5, Sort.by("nome")));
		
		return new ResponseEntity<Page<Usuario>>(usuarios, HttpStatus.OK);
	}
	
	@GetMapping(value="/obter-usuarios-pelo-nome/{nome}", produces = "application/json")
	@CacheEvict(value="cache-obter-usuarios-pelo-nome", allEntries = true)
	@CachePut("cache-obter-usuarios-pelo-nome")
	public ResponseEntity<Page<Usuario>> obterUsuariosPeloNome(@PathVariable("nome") String nome) {
		
		Page<Usuario> usuarios  = null;
		
		if(nome == null || (nome != null & nome.trim().isEmpty()) || nome.equalsIgnoreCase("undefined")) {
			
			usuarios = usuarioRepository.findAll(PageRequest.of(0, 5, Sort.by("nome")));
		}
		else {
			
			usuarios = usuarioRepository.obterUsuariosPeloNome(nome, PageRequest.of(0, 5, Sort.by("nome")));
		}
				
		return new ResponseEntity<Page<Usuario>>(usuarios, HttpStatus.OK);
	}
	
	@GetMapping(value="/obter-usuarios-paginados-pelo-nome/{nome}/pagina/{pagina}", produces = "application/json")
	@CacheEvict(value="cache-obter-usuarios-paginados-pelo-nome", allEntries = true)
	@CachePut("cache-obter-usuarios-paginados-pelo-nome")
	public ResponseEntity<Page<Usuario>> obterUsuariosPaginadosPeloNome(@PathVariable(value = "nome") String nome, @PathVariable(value = "pagina") Long pagina) {
		
		Page<Usuario> usuarios = null;
		
		if(nome == null || (nome != null && nome.trim().isEmpty() || nome.equalsIgnoreCase("undefined"))) {
						
			usuarios = usuarioRepository.findAll(PageRequest.of(pagina.intValue(), 5, Sort.by("nome")));
		}
		else {
			
			usuarios = usuarioRepository.obterUsuariosPeloNome(nome, PageRequest.of(pagina.intValue(), 5, Sort.by("nome")));
		}
		
		return new ResponseEntity<Page<Usuario>>(usuarios, HttpStatus.OK);
	}
	
	
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrarUsuario(@RequestBody Usuario usuario) throws IOException {
		
		for(int i = 0; i < usuario.getTelefones().size(); i ++) {
			usuario.getTelefones().get(i).setUsuario(usuario);
		}
		
		if(usuario.getCep() != null && !usuario.getCep().isEmpty()) {
			/* Consumindo API do ViaCEP - Inicio */
			URL url = new URL("https://viacep.com.br/ws/" + usuario.getCep() + "/json/");
			
			URLConnection connection = url.openConnection();
			
			InputStream inputStream = connection.getInputStream();
			
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			
			String auxiliar = "";
			
			StringBuilder json = new StringBuilder();
			
			while((auxiliar = bufferedReader.readLine()) != null) {
				
				json.append(auxiliar);
			}		
			
			Usuario usuarioAuxiliar = new Gson().fromJson(json.toString(), Usuario.class);
			/* Consumindo API do ViaCEP - Fim */
			
			usuario.setCep(usuarioAuxiliar.getCep());
			
			usuario.setLogradouro(usuarioAuxiliar.getLogradouro());
			
			usuario.setComplemento(usuarioAuxiliar.getComplemento());
			
			usuario.setBairro(usuarioAuxiliar.getBairro());
			
			usuario.setLocalidade(usuarioAuxiliar.getLocalidade());
			
			usuario.setUf(usuarioAuxiliar.getUf());
		}
		
		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		
		usuario.setSenha(senhaCriptografada);
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		if(usuarioSalvo != null) {
			
			implementacaoUserDetailsService.inserirAcessoPadrao(usuarioSalvo.getId());
		}
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}
	
	
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizarUsuario(@RequestBody Usuario usuario) {
		
		for(int i = 0; i < usuario.getTelefones().size(); i ++) {
			usuario.getTelefones().get(i).setUsuario(usuario);
		}
		
		Usuario aux = usuarioRepository.findById(usuario.getId()).get();
		
		/* Caso a senha enviada seja diferente a que se encontra no banco de dados, havera 
		 * uma atualizacao de senha, e a nova senha sera criptografada e salva no banco de dados */
		if(!aux.getSenha().equals(usuario.getSenha())) {
			
			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);
		}
		
		Usuario usuarioAtualizado = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usuarioAtualizado, HttpStatus.OK);
	}
	
	
	@DeleteMapping(value = "/{id}", produces = "application/text")
	public String excluirUsuario(@PathVariable(value = "id") Long id) {
		
		usuarioRepository.deleteById(id);
		
		return "Usuario excluido com sucesso!";
	}
	
	
	@DeleteMapping(value = "/excluir-telefone/{id}", produces = "application/text")
	public String excluirTelefone(@PathVariable("id") Long id) {
		
		telefoneRepository.deleteById(id);
		
		return "Telefone excluido com sucesso!";
	}
	
	@GetMapping(value = "/baixar-relatorio", produces = "application/text")
	public ResponseEntity<String> baixarRelatorio(HttpServletRequest httpServletRequest) {
		
		byte[] pdf = relatorioService.gerarRelatorio("relatorio-usuario", httpServletRequest.getServletContext());
		
		String pdfEmBase64 = "data:application/pdf;base64," + Base64.encodeBase64String(pdf);
		
		return new ResponseEntity<String>(pdfEmBase64, HttpStatus.OK);
	}
	
	@PostMapping(value = "/baixar-relatorio-parametrizado", produces = "application/text")
	public ResponseEntity<String> baixarRelatorioParametrizado(HttpServletRequest httpServletRequest, @RequestBody Relatorio relatorio) {
		
		try {
			
			SimpleDateFormat formatoDoCliente = new SimpleDateFormat("dd/MM/yyyy");
			
			SimpleDateFormat formatoDoJasper = new SimpleDateFormat("yyyy-MM-dd");
			
			String dataInicio = formatoDoJasper.format(formatoDoCliente.parse(relatorio.getDataInicio()));
			
			String dataFim = formatoDoJasper.format(formatoDoCliente.parse(relatorio.getDataFim()));
			
			Map<String, Object> parametros = new HashMap<String, Object>();
			
			parametros.put("DATA_INICIO", dataInicio);
					
			parametros.put("DATA_FIM", dataFim);
			
			byte[] pdf = relatorioService.gerarRelatorioParametrizado("relatorio-usuario-parametrizado", parametros, httpServletRequest.getServletContext());
			
			String pdfEmBase64 = "data:application/pdf;base64," + Base64.encodeBase64String(pdf);
			
			return new ResponseEntity<String>(pdfEmBase64, HttpStatus.OK);
		}
		catch (Exception e) {
			
			e.printStackTrace();
			
			return new ResponseEntity<String>("Falha ao obter relatorio parametrizado", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(value = "/obter-dados-do-grafico", produces = "application/json")
	public ResponseEntity<DadosDoGrafico> obterDadosDoGrafico() {
		
		String nomes = null;
		
		String salarios = null;
		
		List<String> resultado = jdbcTemplate.queryForList("select array_agg(nome) from usuario where salario > 0 and nome <> '' union all select cast(array_agg(salario) as character varying[])from usuario where salario > 0 and nome <> ''", String.class);
		
		if(!resultado.isEmpty()) {
			
			nomes = resultado.get(0).replaceAll("\\{", "").replaceAll("\\}", "").replaceAll("\"", "");
			
			salarios = resultado.get(1).replaceAll("\\{", "").replaceAll("\\}", "");
		}
		
		DadosDoGrafico dadosDoGrafico = new DadosDoGrafico(nomes, salarios);
		
		return new ResponseEntity<DadosDoGrafico>(dadosDoGrafico, HttpStatus.OK);
	}
}
