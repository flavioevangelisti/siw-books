package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.siw.model.Autore;
import it.uniroma3.siw.model.Libro;

@Repository
public interface LibroRepository extends CrudRepository<Libro,Long>{
	
	public List<Libro> findAll();
	
	public boolean existsByTitoloAndAnno(String titolo,Integer anno);
	
	List<Libro> findByAutori(Autore autore);

}