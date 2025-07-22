package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.siw.model.Autore;

@Repository
public interface AutoreRepository extends CrudRepository<Autore,Long>{

	public boolean existsByNomeAndCognome(String nome, String cognome);
}
