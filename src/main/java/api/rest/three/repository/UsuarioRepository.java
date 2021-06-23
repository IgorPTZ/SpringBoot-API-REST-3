package api.rest.three.repository;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import api.rest.three.model.Usuario;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	
	@Query("select u from Usuario u where u.login = ?1")
	Usuario obterUsuarioPorLogin(String login);
	
	@Query("select u from Usuario u where u.nome like %?1%")
	List<Usuario> obterUsuarioPorNome(String nome);
		
	/* Metodo para atualizar o token presente na tabela de usuario durante um novo login */
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "update usuario set token = ?1 where login = ?2")
	void atualizarTokenUser(String token, String login);
	
	@Query(nativeQuery = true, value = "SELECT constraint_name from information_schema.constraint_column_usage " + 
	" WHERE table_name = 'usuarios_role' AND column_name = 'role_id' AND constraint_name <> 'unique_role_user';")
	String obterNomeDaConstraint();
	
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value="INSERT INTO usuarios_role (usuario_id, role_id) VALUES (?1, (SELECT id FROM role WHERE nome_role = 'ROLE_USER'))")
	void inserirRolePadrao(Long usuarioId);
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE usuario set senha = ?1 WHERE id = ?2", nativeQuery = true)
	void modificarSenha(String senha,  Long usuarioId);
	
	default Page<Usuario> obterUsuariosPeloNome(String nome, PageRequest pageRequest) {
		
		Usuario usuario = new Usuario(nome);
		
		// Configurando pesquisa de usuarios paginados por nome
		ExampleMatcher exampleMatcher = ExampleMatcher.matching()
				                                      .withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		
		Example<Usuario> example = Example.of(usuario, exampleMatcher);
		
		Page<Usuario> usuarios = findAll(example, pageRequest);
		
		return usuarios;
	}
}
