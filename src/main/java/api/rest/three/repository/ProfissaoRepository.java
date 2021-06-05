package api.rest.three.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import api.rest.three.model.Profissao;

@Repository
public interface ProfissaoRepository extends JpaRepository<Profissao, Long>{

}
