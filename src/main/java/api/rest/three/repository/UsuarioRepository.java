package api.rest.three.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import api.rest.three.model.Usuario;


@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long>{
	
	@Query("select u from Usuario u where u.login = ?1")
	Usuario findUserByLogin(String login);
	
	@Query("select u from Usuario u where u.nome like %?1%")
	List<Usuario> findUserByNome(String nome);
		
	/* Metodo para atualizar o token presente na tabela de usuario durante um novo login */
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "update usuario set token = ?1 where login = ?2")
	void atualizarTokenUser(String token, String login);
}
